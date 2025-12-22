# Converting Repository Methods to Query Pattern

## Overview
This guide describes the process for converting direct repository implementations to use the Query pattern. This pattern promotes separation of concerns by encapsulating query logic into dedicated query and handler classes.

## When to Apply
**Only convert pure read operations** - methods that:
- Only retrieve data without modifying state
- Don't have side effects
- Return data based on input parameters or return all records

**Do NOT convert** operations that:
- Modify data (create, update, delete)
- Have side effects
- Evaluate or process data beyond simple retrieval

## Process Steps

### 1. Create Query Class
Create a simple POJO in the `persistence/query` package to encapsulate query parameters.

**Location:** `api/src/main/java/com/explik/diybirdyapp/persistence/query/`

**Template:**
```java
package com.explik.diybirdyapp.persistence.query;

public class Get[Entity][Criteria]Query {
    // Add fields for query parameters
    private String parameterName;
    
    // Generate getters and setters
    public String getParameterName() {
        return parameterName;
    }
    
    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }
}
```

**Example - Query with no parameters:**
```java
package com.explik.diybirdyapp.persistence.query;

public class GetAllExercisesQuery {
    // No parameters needed for a simple getAll operation
}
```

### 2. Create Query Handler
Create a handler class that implements `QueryHandler<TQuery, TResult>` interface.

**Location:** `api/src/main/java/com/explik/diybirdyapp/persistence/query/handler/`

**Template:**
```java
package com.explik.diybirdyapp.persistence.query.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
// Add other necessary imports

@Component
public class Get[Entity][Criteria]QueryHandler implements QueryHandler<Get[Entity][Criteria]Query, [ReturnType]> {
    @Autowired
    GraphTraversalSource traversalSource;

    // Inject other required dependencies
    
    @Override
    public [ReturnType] handle(Get[Entity][Criteria]Query query) {
        // Move the existing implementation logic here
        // Extract query parameters from the query object
        // Perform the read operation
        // Return the result
    }
}
```

**Example:**
```java
package com.explik.diybirdyapp.persistence.query.handler;

import com.explik.diybirdyapp.model.exercise.ExerciseDto;
import com.explik.diybirdyapp.persistence.ExerciseRetrievalContext;
import com.explik.diybirdyapp.persistence.modelFactory.ContextualModelFactory;
import com.explik.diybirdyapp.persistence.provider.GenericProvider;
import com.explik.diybirdyapp.persistence.query.GetAllExercisesQuery;
import com.explik.diybirdyapp.persistence.vertex.ExerciseVertex;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetAllExercisesQueryHandler implements QueryHandler<GetAllExercisesQuery, List<ExerciseDto>> {
    @Autowired
    GraphTraversalSource traversalSource;

    @Autowired
    private GenericProvider<ContextualModelFactory<ExerciseVertex, ExerciseDto, ExerciseRetrievalContext>> exerciseModelFactoryProvider;

    @Override
    public List<ExerciseDto> handle(GetAllExercisesQuery query) {
        // Null indicates generic exercise model factory
        var factory = exerciseModelFactoryProvider.get(null);
        var vertices = ExerciseVertex.getAll(traversalSource);

        return vertices
                .stream()
                .map(e -> factory.create(e, ExerciseRetrievalContext.createDefault()))
                .toList();
    }
}
```

### 3. Update Repository Implementation
Replace the direct implementation with query handler invocation.

**Steps:**
1. Add import statements for the query and handler classes
2. Inject the query handler as a field
3. Replace the method implementation with query creation and handler invocation

**Before:**
```java
@Override
public List<ExerciseDto> getAll() {
    // Direct implementation
    var factory = exerciseModelFactoryProvider.get(null);
    var vertices = ExerciseVertex.getAll(traversalSource);

    return vertices
            .stream()
            .map(e -> factory.create(e, ExerciseRetrievalContext.createDefault()))
            .toList();
}
```

**After:**
```java
// Add imports
import com.explik.diybirdyapp.persistence.query.GetAllExercisesQuery;

// Add field injection
@Autowired
private QueryHandler<GetAllExercisesQuery, List<ExerciseDto>> getAllExercisesQueryHandler;

// Update method
@Override
public List<ExerciseDto> getAll() {
    var query = new GetAllExercisesQuery();
    return getAllExercisesQueryHandler.handle(query);
}
```

### 4. Update Repository Interface (if needed)
If the method signature changes (e.g., return type), update the repository interface accordingly.

## Benefits
- **Separation of Concerns:** Query logic is isolated from repository orchestration
- **Testability:** Handlers can be tested independently
- **Reusability:** Query handlers can be reused across different contexts
- **Maintainability:** Easier to locate and modify specific query logic

## Examples in Codebase
- **Query:** `GetExerciseByIdsQuery` - Query with parameters (id, sessionId)
- **Handler:** `GetExerciseByIdsQueryHandler` - Handles single exercise retrieval
- **Query:** `GetAllExercisesQuery` - Query without parameters
- **Handler:** `GetAllExercisesQueryHandler` - Handles all exercises retrieval

## Notes
- Always annotate handlers with `@Component` for Spring dependency injection
- Use `@Autowired` for dependency injection in handlers
- The `QueryHandler` interface is generic: `QueryHandler<TQuery, TResult>`
- Keep query classes simple POJOs with just fields and getters/setters
- Move all business logic from repository to the handler
