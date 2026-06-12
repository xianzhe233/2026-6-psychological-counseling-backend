# Phase 4 consultation API integration test (reset state required)
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

$env:MYSQL_PWD = "123456"
$dutyDate = (mysql -uroot psychological_counseling -N -e "SELECT duty_date FROM duty_schedule WHERE staff_type='COUNSELOR' LIMIT 1" 2>$null).Trim()

# 1. Login
$login = Invoke-ApiExpectCode POST "/api/auth/login" @{ username = "assistant"; password = "123456" } 200
Log-Test "1. login assistant" $true "role=$($login.data.primaryRole)"

# 2. Queue page
$queue = Invoke-ApiExpectCode GET "/api/assistant/consultation/queue?pageNum=1&pageSize=10" $null 200
Log-Test "2. queue page total=3" ($queue.data.total -eq 3) "total=$($queue.data.total)"
Log-Test "3. queue sort URGENT first" ($queue.data.records[0].crisisLevel -eq "URGENT") "first=$($queue.data.records[0].crisisLevel)"

# 3. Detail
$detail = Invoke-ApiExpectCode GET "/api/assistant/consultation/queue/3" $null 200
Log-Test "4. queue detail" ($detail.data.studentNo -eq "20230001") "studentNo=$($detail.data.studentNo)"

# 4. Available slots
$slots = Invoke-ApiExpectCode GET "/api/assistant/counselors/available-slots?counselorId=4&startDate=$dutyDate" $null 200
$avail = @($slots.data | Where-Object { $_.available -eq $true }).Count
Log-Test "5. available slots" ($slots.data.Count -ge 2 -and $avail -ge 2) "date=$dutyDate count=$($slots.data.Count) available=$avail"

# 5. Arrange queue 3
$arrange = Invoke-ApiExpectCode POST "/api/assistant/consultation/schedules" @{
    queueId = 3; studentId = 2; counselorId = 4
    consultationDate = $dutyDate; slotId = 1; roomId = 1; remark = "test"
} 200
$scheduleId = $arrange.data.id
Log-Test "6. arrange consultation" ($arrange.data.scheduleNo -like "CS*") "id=$scheduleId no=$($arrange.data.scheduleNo)"

# 6. Queue ARRANGED
$q3 = Invoke-ApiExpectCode GET "/api/assistant/consultation/queue/3" $null 200
Log-Test "7. queue status ARRANGED" ($q3.data.queueStatus -eq "ARRANGED") "status=$($q3.data.queueStatus)"

# 7. Schedule list
$list = Invoke-ApiExpectCode GET "/api/assistant/consultation/schedules?pageNum=1&pageSize=10" $null 200
Log-Test "8. schedule list" ($list.data.total -ge 1) "total=$($list.data.total)"

# 8. Student conflict 409
$conflict = Invoke-Api POST "/api/assistant/consultation/schedules" @{
    queueId = 2; studentId = 2; counselorId = 4
    consultationDate = $dutyDate; slotId = 1; roomId = 2
} $null
Log-Test "9. student conflict 409" ($conflict.code -eq 409 -and $conflict.data.conflicts.Count -ge 1) "code=$($conflict.code)"

# 9. Counselor conflict 409 (queue 2 different student impossible - same student slot2 ok)
$arrange2 = Invoke-ApiExpectCode POST "/api/assistant/consultation/schedules" @{
    queueId = 2; studentId = 2; counselorId = 4
    consultationDate = $dutyDate; slotId = 2; roomId = 1
} 200
Log-Test "10. arrange slot2 queue2" ($arrange2.data.id -gt 0) "id=$($arrange2.data.id)"

# 10. Suspend
$suspend = Invoke-ApiExpectCode POST "/api/assistant/consultation/queue/1/suspend" @{ reason = "defer" } 200
$q1 = Invoke-ApiExpectCode GET "/api/assistant/consultation/queue/1" $null 200
Log-Test "11. suspend queue" ($q1.data.queueStatus -eq "SUSPENDED") "status=$($q1.data.queueStatus)"

# 11. Cancel first schedule
$cancel = Invoke-ApiExpectCode POST "/api/assistant/consultation/schedules/$scheduleId/cancel" @{ reason = "cancel test" } 200
$q3c = Invoke-ApiExpectCode GET "/api/assistant/consultation/queue/3" $null 200
Log-Test "12. cancel schedule" $true "scheduleId=$scheduleId"
Log-Test "13. queue revert WAITING" ($q3c.data.queueStatus -eq "WAITING") "status=$($q3c.data.queueStatus)"

# 12. Not found 404
$nf = Invoke-Api GET "/api/assistant/consultation/queue/99999" $null
Log-Test "14. queue not found 404" ($nf.code -eq 404) "code=$($nf.code)"

Write-Host ""
Write-Host "========== SUMMARY =========="
$passed = @($results | Where-Object { $_.Pass }).Count
$failed = @($results | Where-Object { -not $_.Pass }).Count
Write-Host "Passed: $passed / $($results.Count), Failed: $failed"
$results | Format-Table -AutoSize
if ($failed -gt 0) { exit 1 }
