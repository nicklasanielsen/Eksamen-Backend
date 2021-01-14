package DTOs;

/**
 *
 * @author Nicklas Nielsen
 */
public class JokeDTO {

    private String ref;
    private String joke;

    public JokeDTO(String ref, String joke) {
        this.ref = ref;
        this.joke = joke;
    }

    public String getRef() {
        return ref;
    }

    public String getJoke() {
        return joke;
    }

}
