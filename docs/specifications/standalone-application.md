# Standardalone application specification (TO COME LATER)
The backend can be run in a limited fasion as a local application without a database, where all data is persisted as JSON files on dist. On start-up, all commands stored in the JSON files are replayed to reconstruct the in-memory state of the app, including decks, flashcards, and session progress. This allows quick setup for applications that only require limited data availability, like serving tests or a limited amount of users. 

Pros: 
- No database setup required, making deployment very simple. 
- Data is stored in serialized format allowing for replication on main server. 

Cons: 
- Limited data availability, as the application relies on an in-memory state for all operations.
- Increasingly slow start-up times as the amount of data grows, since all commands need to be replayed to reconstruct the state.

## Persistence 
All system data is stored as a series of serialized commands in a JSON file on disk. Each command represents a specific action taken by the user, such as creating a new deck, adding a flashcard, or updating session progress. When a command is executed, it is serialized and appended to the JSON file, ensuring that all changes are recorded in a persistent manner.

On startup, the application reads the JSON file and deserializes each command in sequence to reconstruct the in-memory state of the app. This process allows the application to restore all decks, flashcards, and session progress exactly as they were before the last shutdown, providing a seamless user experience without the need for a traditional database.

## Data sharing
The smaller stand-alone application can be connected larger central server to share data across multiple instances. This can be archieved by relaying the serialized commands to the central server, which can then apply them to its own state and distribute them to other connected instances. This approach allows for real-time data synchronization between the standalone application and the central server, enabling centralized persistence and higher performance using native-db performance while still allowing the standalone application to operate independently when needed. This hybrid approach provides flexibility for users who want the simplicity of a standalone application with the benefits of centralized data management.

## Supported scenarios

### Initial setup of application
The replay mechanism allows for quick setup of the application without the need for manual data entry. By simply providing a JSON file with the necessary commands, users can quickly initialize the application with pre-defined decks, flashcards, and session progress. This is particularly useful for testing purposes or for users who want to start with a specific set of data.

### Testing and development
The standalone application is ideal for testing and development scenarios, as it allows developers to quickly iterate on features and functionality without the overhead of setting up a database. The replay mechanism ensures that all changes are persisted, making it easy to track and debug issues during development. Additionally, the ability to share data with a central server allows for testing of synchronization and data sharing features without the need for a full deployment.

### Edge-computing scenarios
In edge-computing scenarios, where connectivity to a central server may be limited or unreliable, the standalone application can operate independently while still allowing for data sharing when connectivity is available. The replay mechanism ensures that all changes are persisted locally, and when connectivity is restored, the serialized commands can be relayed to the central server for synchronization. This allows users in remote or offline environments to continue using the application without interruption while still benefiting from centralized data management when possible.