# Contributing to Manchester Seals

Thank you for your interest in contributing to the Manchester Seals Java-Python project!

## Development Setup

### Prerequisites
- Java 17+
- Maven 3.8.1+
- Git

### Clone and Setup
```bash
git clone https://github.com/manchesterseals/manchester-seals-java-python.git
cd manchester-seals-java-python
```

### Build the Project
```bash
mvn clean package
```

### Run Tests
```bash
mvn test
```

## Development Workflow

### Creating a Feature Branch
```bash
git checkout -b feature/your-feature-name
```

### Making Changes
1. Make your changes
2. Commit with clear messages
3. Push your branch
4. Create a Pull Request

### Code Style
- Follow Java conventions
- Add unit tests for new features
- Update documentation as needed
- Ensure all tests pass before submitting PR

## Testing

All code must pass:
- Unit tests: `mvn test`
- Build: `mvn clean package`
- 51 total tests in the test suite

## Pull Request Process

1. Create feature branch from `main`
2. Make your changes
3. Commit with meaningful messages
4. Push to GitHub
5. Create Pull Request with description
6. Wait for review and CI/CD checks
7. Address any feedback
8. Merge when approved

## Project Structure

```
src/
├── main/java/com/manchesterseals/
│   ├── ManchesterSealsApplication.java
│   ├── controller/
│   ├── service/
│   ├── model/
│   └── util/
└── test/java/com/manchesterseals/
    ├── Controller tests
    ├── Service tests
    └── Utility tests
```

## Questions?

- Check existing issues: https://github.com/manchesterseals/manchester-seals-java-python/issues
- Review documentation: README.md and TEST_SUMMARY.md
- Open a new issue for questions

Thank you for contributing!

