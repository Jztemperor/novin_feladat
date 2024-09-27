package com.md.backend.validator;

import com.md.backend.annotation.DueDateAfterIssueDate;
import com.md.backend.dto.Invoice.CreateInvoiceRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DueDateAfterIssueDateValidator implements ConstraintValidator<DueDateAfterIssueDate, CreateInvoiceRequest> {

    @Override
    public boolean isValid(CreateInvoiceRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }

        // Check if dueDate is after issueDate
        boolean isValid = request.getDueDate() != null && request.getIssueDate() != null &&
                request.getDueDate().isAfter(request.getIssueDate());

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Az esedékesség dátumának a kiállítási dátum utáninak kell lennie!")
                    .addPropertyNode("dueDate")
                    .addConstraintViolation();
        }

        return isValid;
    }
}
