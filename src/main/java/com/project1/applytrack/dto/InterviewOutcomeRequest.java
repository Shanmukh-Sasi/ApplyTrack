package com.project1.applytrack.dto;

import com.project1.applytrack.model.InterviewOutcome;
import jakarta.validation.constraints.NotNull;

public class InterviewOutcomeRequest {

    @NotNull(message = "Interview outcome is required")
    private InterviewOutcome outcome;

    public InterviewOutcome getOutcome() {
        return outcome;
    }

    public void setOutcome(InterviewOutcome outcome) {
        this.outcome = outcome;
    }
}
