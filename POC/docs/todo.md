## Email
- `EmailMessageId` value object does no validation. It also does not follow the rules of a real EmailMessageId
- Some kind of repositoryAssert using a RecursiveComparison?
- Expose meer uitleg over de email parsing fout naar eindgebruiker?
- Introduce BufferedEventPublisher to ensure correct order of `save/publish/presenter.success()`?
- Some kind of eventHandlerAssert - now `MockEventPublisher` is both a spy and a dispatcher
- Detectiekenmerk contract?


## Important
- RegistreerEmail ingests all types of files, even though the application only supports `.eml` and `.txt`
  - this should be checked in the domain and not in the `cli-adapter`