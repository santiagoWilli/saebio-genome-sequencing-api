package payloads;

public class EmptyPayload implements Validable {
    @Override
    public boolean isValid() {
        return true;
    }
}
