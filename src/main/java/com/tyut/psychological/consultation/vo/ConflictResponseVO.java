package com.tyut.psychological.consultation.vo;

import java.util.List;

public class ConflictResponseVO {
    private List<ConflictInfoVO> conflicts;

    public ConflictResponseVO() {
    }

    public ConflictResponseVO(List<ConflictInfoVO> conflicts) {
        this.conflicts = conflicts;
    }

    public List<ConflictInfoVO> getConflicts() { return conflicts; }
    public void setConflicts(List<ConflictInfoVO> conflicts) { this.conflicts = conflicts; }
}
