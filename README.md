# MCS Backend

BFF layer for MCS app

## Running

To run locally, you'll need to add the `MCS_API_KEY` to your local system environment (`~/.zshrc` file or similar)

Execute this command

```bash
./gradlew run
```

Then, navigate to [http://localhost:8080/](http://localhost:8080/) (or whatever your defined route is)

Or run the main function

## Documentation
1. Export postman collection
1. Install postman-to-openapi - `npm i postman-to-openapi -g` - https://github.com/joolfe/postman-to-openapi
1. `p2o MCSBFF.postman_collection.json -f documentation.yml`
