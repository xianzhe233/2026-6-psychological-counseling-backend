# Phase 5 counselor API integration test
$Base = "http://localhost:24681"
$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$results = @()
$ErrorActionPreference = "Stop"

function Log-Test($name, $ok, $detail) {
    $script:results += [PSCustomObject]@{ Test = $name; Pass = [bool]$ok; Detail = "$detail" }
    $mark = if ($ok) { "PASS" } else { "FAIL" }
    Write-Host "[$mark] $name - $detail"
}

function Invoke-Api($method, $uri, $body) {
    $params = @{
        Uri = "$Base$uri"
        Method = $method
        WebSession = $session
        ContentType = "application/json; charset=utf-8"
        UseBasicParsing = $true
    }
    if ($null -ne $body) { $params.Body = ($body | ConvertTo-Json -Compress) }
    return Invoke-RestMethod @params
}

function Invoke-ApiExpectCode($method, $uri, $body, $expectCode) {
    $resp = Invoke-Api $method $uri $body
    if ($resp.code -ne $expectCode) {
        throw "Expected code $expectCode but got $($resp.code): $($resp.message)"
    }
    return $resp
}

# 1. Login counselor
$login = Invoke-ApiExpectCode POST "/api/auth/login" @{ username = "counselor"; password = "123456" } 200
Log-Test "1. login counselor" $true "role=$($login.data.primaryRole)"

# 2. My schedules
$schedules = Invoke-ApiExpectCode GET "/api/counselor/schedules?pageNum=1&pageSize=10" $null 200
Log-Test "2. counselor schedules" ($schedules.data.total -ge 1) "total=$($schedules.data.total)"
$scheduleId = $schedules.data.records[0].id

# 3. Schedule detail
$detail = Invoke-ApiExpectCode GET "/api/counselor/schedules/$scheduleId" $null 200
Log-Test "3. schedule detail" ($detail.data.id -eq $scheduleId) "id=$($detail.data.id)"

# 4. Save consultation record
$record = Invoke-ApiExpectCode POST "/api/counselor/schedules/$scheduleId/record" @{
    recordStatus = "COMPLETED"
    consultationTime = "2026-06-12 10:00:00"
    contentSummary = "围绕考试压力进行讨论。"
    nextPlan = "下次继续讨论睡眠改善。"
    needClose = 0
} 200
Log-Test "4. save record" ($record.data.recordStatus -eq "COMPLETED") "status=$($record.data.recordStatus)"

# 5. Get record
$getRecord = Invoke-ApiExpectCode GET "/api/counselor/schedules/$scheduleId/record" $null 200
Log-Test "5. get record" ($getRecord.data.scheduleId -eq $scheduleId) "scheduleId=$($getRecord.data.scheduleId)"

# 6. Create extension request
$ext = Invoke-ApiExpectCode POST "/api/counselor/extension-requests" @{
    studentId = 2
    requestSessions = 2
    reason = "既有咨询安排完成后仍需继续支持。"
} 200
Log-Test "6. create extension" ($ext.data.status -eq "PENDING") "id=$($ext.data.id)"
$extId = $ext.data.id

# 7. Extension list
$extList = Invoke-ApiExpectCode GET "/api/counselor/extension-requests?pageNum=1&pageSize=10" $null 200
Log-Test "7. extension list" ($extList.data.total -ge 1) "total=$($extList.data.total)"

# 8. Save case report draft
$report = Invoke-ApiExpectCode POST "/api/counselor/case-reports" @{
    studentId = 2
    problemTypeId = 1
    totalSessions = 2
    effectSelfRating = "较好"
    caseSummary = "学生主要困扰为考试压力。"
    counselingEffect = "焦虑感有所下降。"
    suggestion = "建议继续保持规律作息。"
    closeType = "NORMAL"
    reportStatus = "DRAFT"
} 200
Log-Test "8. save case report draft" ($report.data.reportStatus -eq "DRAFT") "id=$($report.data.id)"
$reportId = $report.data.id

# 9. Submit case report
$submit = Invoke-ApiExpectCode POST "/api/counselor/case-reports/$reportId/submit" $null 200
Log-Test "9. submit case report" ($submit.data.reportStatus -eq "SUBMITTED") "status=$($submit.data.reportStatus)"

# 10. Admin login and view submitted reports
$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$adminLogin = Invoke-ApiExpectCode POST "/api/auth/login" @{ username = "admin"; password = "123456" } 200
Log-Test "10. login admin" $true "role=$($adminLogin.data.primaryRole)"

$adminReports = Invoke-ApiExpectCode GET "/api/admin/case-reports?pageNum=1&pageSize=10" $null 200
Log-Test "11. admin view submitted reports" ($adminReports.data.total -ge 1) "total=$($adminReports.data.total)"

$adminExt = Invoke-ApiExpectCode GET "/api/admin/extension-requests?pageNum=1&pageSize=10&status=PENDING" $null 200
Log-Test "12. admin extension list" ($adminExt.data.total -ge 1) "total=$($adminExt.data.total)"

Invoke-ApiExpectCode POST "/api/admin/extension-requests/$extId/approve" $null 200
Log-Test "13. admin approve extension" $true "id=$extId"

$passed = @($results | Where-Object { $_.Pass }).Count
$failed = @($results | Where-Object { -not $_.Pass }).Count
Write-Host "`nSummary: $passed passed, $failed failed"
if ($failed -gt 0) { exit 1 }
