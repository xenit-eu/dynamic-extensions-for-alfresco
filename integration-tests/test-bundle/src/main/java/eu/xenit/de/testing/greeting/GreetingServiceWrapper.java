package eu.xenit.de.testing.greeting;

public class GreetingServiceWrapper {

    private final GreetingService greetingService;

    public GreetingServiceWrapper(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    public GreetingService getGreetingService() {
        return greetingService;
    }
}
