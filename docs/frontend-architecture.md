# Frontend architecture

## Overall concepts 
The frontend is split into a series of module: 
- `shared`: responsible for shared components, models, and services
- `content`: responsible for displaying the content pages
- `exercise`: responsible for displaying the exercise pages

Each module has its own data models (DTOs), so the language model for content and exercise modules are seperated. 
Even though they represent the same underlying data. This allows the server data format to match the UI closely. 

The frontend uses an optional MVVM architecture. 
The components can either use DTO directly or use a ViewModel that is a wrapper around the DTO.
The view model can be used to add computed properties, methods, and other client-side data that is not directly related to the DTO.

The components are split into two categories:
- Visual components: responsible for rendering the UI
- Data-driven components: responsible for managing the data and the business logic

The seperation of visual components allow for overview pages: 
[hostname]/docs/shared-components: shared components that are used across multiple pages
[hostname]/docs/content-components: components that are used for the content pages 
[hostname]/docs/exercise-components: components that are used for the exercise pages