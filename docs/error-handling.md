# 🧩 Error Handling Architecture

This document explains how error handling works across our **Java Spring Boot backend** and **Angular frontend** — and how to extend it safely when adding new validation rules or features.

---

## ⚙️ Overview

Our validation and error handling are designed to provide **consistent, machine-readable responses** to the frontend, while keeping **clear separation of concerns**:

| Layer | Responsibility | Example |
|--------|----------------|----------|
| **DTO (Java)** | Input structure & field validation | `@NotBlank`, `@Email` |
| **Service Layer** | Business / domain validation | “Email already taken”, “Username conflict” |
| **Global Exception Handler** | Formats all validation errors into a unified JSON response | `{ type, fields }` |
| **Angular Frontend** | Maps server-side errors to form controls and displays localized messages | `control.setErrors({ 'email.alreadyTaken': true })` |

---

## 🧱 1. Validation Flow (Backend)

### Step 1 — DTO Validation

Each REST request DTO is annotated with **Bean Validation (JSR-380)** annotations.  
Example:

```java
@FieldsMatch(field = "password", fieldMatch = "confirmPassword", message = "password.mismatch")
public class UserRegistrationRequest {

    @NotBlank(message = "email.required")
    @Email(message = "email.invalid")
    private String email;

    @NotBlank(message = "password.required")
    @Size(min = 6, message = "password.tooShort")
    private String password;

    private String confirmPassword;
}
```

Spring automatically validates this before the controller runs (via `@Valid`).  
If validation fails, Spring throws a `MethodArgumentNotValidException`.

---

### Step 2 — Service Validation

Once DTO validation passes, the service layer performs **business logic validation**:
- Uniqueness checks (e.g., email already exists)
- Cross-entity or contextual rules
- External API validation

Example:

```java
@Service
public class UserService {

    public void registerUser(UserRegistrationRequest req) {
        Map<String, String> errors = new HashMap<>();

        if (emailExists(req.getEmail())) {
            errors.put("email", "email.alreadyTaken");
        }

        if (isWeakPassword(req.getPassword())) {
            errors.put("password", "password.weak");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // continue with registration...
    }
}
```

---

### Step 3 — Global Exception Handling

All validation exceptions are handled by a single `@RestControllerAdvice`:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(err ->
            fields.put(err.getField(), err.getDefaultMessage())
        );

        ex.getBindingResult().getGlobalErrors().forEach(err ->
            fields.put("_form", err.getDefaultMessage())
        );

        return buildValidationErrorResponse(fields);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleServiceValidation(ValidationException ex) {
        return buildValidationErrorResponse(ex.getFields());
    }

    private ResponseEntity<Map<String, Object>> buildValidationErrorResponse(Map<String, String> fields) {
        Map<String, Object> body = new HashMap<>();
        body.put("type", "validation-error");
        body.put("fields", fields);
        return ResponseEntity.badRequest().body(body);
    }
}
```

**Unified Response Example:**

```json
{
  "type": "validation-error",
  "fields": {
    "email": "email.alreadyTaken",
    "_form": "password.mismatch"
  }
}
```

---

## 🧠 2. Frontend Integration (Angular)

The Angular frontend expects this structured format.

### Intercepting Validation Errors

In your form submission logic:

```typescript
onSubmit() {
  this.http.post('/api/users/register', this.registerForm.value).subscribe({
    next: () => this.form.reset(),
    error: (err) => this.handleServerErrors(err.error)
  });
}

private handleServerErrors(err: any) {
  if (err?.type === 'validation-error') {
    Object.entries(err.fields).forEach(([key, code]) => {
      const control = this.registerForm.get(key);
      if (control) {
        control.setErrors({ [code]: true });
      } else if (key === '_form') {
        this.registerForm.setErrors({ [code]: true });
      }
    });
  }
}
```

### Hardcoded i18n Messages

Each field’s errors are defined in the template for i18n consistency:

```html
<div *ngIf="form.get('email')?.errors as errors">
  <span *ngIf="errors['email.required']" i18n>Email is required</span>
  <span *ngIf="errors['email.invalid']" i18n>Invalid email format</span>
  <span *ngIf="errors['email.alreadyTaken']" i18n>This email is already taken</span>
</div>
```

---

## 🧩 3. Adding New Validations

### (A) Add a Field-Level Validation (Annotation)

1. Create a new annotation:
   ```java
   @Constraint(validatedBy = StrongPasswordValidator.class)
   public @interface StrongPassword {
       String message() default "password.weak";
   }
   ```
2. Implement the validator:
   ```java
   public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {
       public boolean isValid(String value, ConstraintValidatorContext ctx) {
           return value != null && value.matches(".*\\d.*");
       }
   }
   ```
3. Use it in your DTO:
   ```java
   @StrongPassword
   private String password;
   ```

---

### (B) Add a Service-Level Validation

If validation depends on external data (DB, API, etc.), add logic in your service and throw `ValidationException`:

```java
if (usernameTaken(req.getUsername())) {
    throw new ValidationException(Map.of("username", "username.alreadyTaken"));
}
```

---

### (C) Add Client-Side Messages

In the Angular form template, add the new error key:

```html
<span *ngIf="errors['username.alreadyTaken']">Username already taken</span>
```

---

## 🧩 4. Error Response Format (Contract)

| Key | Type | Description |
|------|------|-------------|
| `type` | string | Always `"validation-error"` for validation issues |
| `fields` | object | Field → error code mapping |
| `_form` | string (optional) | Used for cross-field or global validation errors |

Example:
```json
{
  "type": "validation-error",
  "fields": {
    "email": "email.invalid",
    "password": "password.tooShort",
    "_form": "password.mismatch"
  }
}
```

---

## 💡 5. Best Practices

- ✅ Always use **error codes** (`email.invalid`) instead of plain messages — frontend handles translation or message display.
- ✅ Keep **DTO validation** for syntactic and formatting rules.
- ✅ Use **service validation** for business and data integrity rules.
- ✅ Never duplicate validation messages between backend and frontend — rely on error codes.
- ✅ When adding a new validation, test both:
  - Input failing `@Valid`
  - Input passing `@Valid` but failing business validation

---

## 🧩 6. Common Extension Points

| Task | File to Modify | Notes |
|------|----------------|-------|
| Add new field validator | `src/main/java/.../validation/` | Create new `@Constraint` + Validator class |
| Add business rule | `UserService.java` (or appropriate service) | Throw `ValidationException` |
| Add global validation | Class-level annotation (e.g., `@FieldsMatch`) | Use `ObjectError` via `@Constraint(validatedBy=...)` |
| Add new error message | Angular template | Use the error code key |
| Change response format | `GlobalExceptionHandler.java` | Modify `buildValidationErrorResponse()` |

---

## 🧪 7. Example End-to-End Flow

1. User submits registration form.  
2. Angular sends `POST /api/users/register`.  
3. Spring validates DTO → fails → returns:
   ```json
   {
     "type": "validation-error",
     "fields": {
       "email": "email.invalid"
     }
   }
   ```
4. Angular maps `email.invalid` → shows localized message.  
5. Developer adds new field → adds new annotation, validator, and message → done.

---

## 🧭 Summary

✅ Consistent validation model  
✅ Clear separation between syntax (DTO) and business (service) rules  
✅ Unified backend error contract  
✅ Simple Angular integration for i18n and UX

---

> **In short:**  
> - Use `@Valid` for input validation  
> - Throw `ValidationException` for business rules  
> - Always return `{ type: "validation-error", fields: { ... } }`  
> - Angular will handle the rest.
