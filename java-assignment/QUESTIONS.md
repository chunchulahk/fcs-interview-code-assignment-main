# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?
- Store and Product modules access database directly from REST layer
- Warehouse module follows clean architecture (REST → use-case → repository)
- This creates inconsistency in code base
- If I maintain this project, I will refactor Store and Product
- I will follow same structure as Warehouse for better readability and testing


2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

- Warehouse API uses OpenAPI and code generation
- It gives clear API contract and avoids mismatch
- Good when multiple teams work on same API

- Product and Store APIs are coded directly
- Faster for small APIs but no strict contract
- Harder to maintain in long run

- My choice: OpenAPI for core APIs like Warehouse
- Direct coding only for small or internal APIs

3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?
- First focused on unit tests for use-case layer
- Used fake ports to test business logic without database
- Unit tests are fast and reliable

- Added limited integration tests for REST and DB flow
- Avoided too many integration tests to save time

- Used JaCoCo to track code coverage
- Coverage can be maintained above 80% by adding tests for new logic
