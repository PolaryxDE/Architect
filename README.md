# Architect
Architect is a Java Framework allowing simple dependency injection and sharing of managers and singleton between classes. 

## Usage
Architect adds a powerful feature but because its not that complex, the usage of Architect is very simple.

**First:** Create a Service.
```java
public class PersonService implements IService {
	
	public Person getPerson(String name) {}
}
```
**Second:** Add a new Service which uses the first Service.
```java
public class MainService implements IService {
	
	@Override
	public void start() {
	}
}
```
As you can see here, we can optionally override a `start` method which gets called when Architect starts.

**Third:** Adding an Instance field to access the first Service.
```java
public class MainService implements IService {
	
	@Instance private PersonService personService;
	
	@Override
	public void start() {
		Person me = this.personService.getPerson("me");
	}
}
```
**Last:** Build the Architect and start the whole System.
```java
public static void main(String[] args) {
	Architect architecht = new Architect()
		.register(PersonService.class)
		.register(MainService.class)
		.start();
	// Running logic here
	architect.stop();
}
```

Architect will initialize all Services, fill all `@Instance` fields and start the Services.

### Events
One optional thing Architect has to offer, are events. Events can be registered which will be called when either a Service was `CREATED` (before the Service is being started but after the instance was created) or `STOPPED` (after the Instances has be stopped and removed from the System). If this happens a Callback can be called which can do something with the Service. Just add an `on()` statement before starting the Architect.
```java
public static void main(String[] args) {
	Architect architecht = new Architect()
		.register(PersonService.class)
		.register(MainService.class)
		.on(Events.CREATED, service -> ...)
		.start();
	// Running logic here
	architect.stop();
}
```
