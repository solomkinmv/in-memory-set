# In Memory Set Service

Implemented using array and open addressing hash table.

## Development commands

### Build Docker image

Regular:
```bash
 ./mvnw spring-boot:build-image
```

Native:
```bash
./mvnw -Pnative spring-boot:build-image
```

### Publish Docker image

```bash
docker push --all-tags solomkinmv/in-memory-set-service
```
