package eu.xenit.de.testing.greeting;

public class GreetingService {

    private static int numberOfInstances;

    public GreetingService() {
        numberOfInstances++;
    }

    public String getGreeting() {
        return "Hello there, adventurer!";
    }

    public static int getNumberOfInstances() {
        return numberOfInstances;
    }

}
