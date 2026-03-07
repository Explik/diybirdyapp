# Testing strategy
## Unit tests
Unit test should be written for all parts of the code base, whereever feasable. 

## Integration tests
Integration tests (for the backend controllers) should be written for all features mentioned in the roadmap.

## Component tests
Component tests should be written for all components in the application using Cypress Component Testing. 

**Best practices:**
- Import components and dependencies at the top of each test file
- Test each component's functionality only once; avoid duplicating tests for nested components
- Extract magic strings into constants or variables to prevent repetition
- Move repeated or complex actions and assertions into helper functions
- Focus on the component's own behavior rather than its children's implementations
- Use descriptive test names that clearly indicate what is being tested
- Test names should match any specifications or requirements related to the component's functionality

## End-to-end tests
End-to-end tests should be written for all features mentioned in the roadmap.
End-to-end tests are written using Cypress.

