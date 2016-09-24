package mapping;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 *
 * Used to help the Gson library write classes with Abstract superclasses or interfaces to JSON files.
 *
 * @param <T> The interface/abstract class type the collection needs to adapt for.
 * @author Matthew Gimbut
 *
 */

public class InterfaceAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {

    @Override
    public JsonElement serialize(T object, Type interfaceType, JsonSerializationContext context) {
        JsonObject member = new JsonObject();
        member.addProperty("type", object.getClass().getName());
        member.add("data", context.serialize(object));
        return member;
    }

    @Override
    public T deserialize(JsonElement elem, Type interfaceType, JsonDeserializationContext context) throws JsonParseException {
        JsonObject member = (JsonObject) elem;
        JsonElement typeString = get(member, "type");
        JsonElement data = get(member, "data");
        Type actualType = typeForName(typeString);

        return context.deserialize(data, actualType);
    }

    private Type typeForName(JsonElement typeElem) {
        try {
            return Class.forName(typeElem.getAsString());
        }
        catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }

    private JsonElement get(JsonObject wrapper, String memberName) {
        JsonElement elem = wrapper.get(memberName);
        if (elem == null) {
            throw new JsonParseException("no '" + memberName + "' member found in json file.");
        }
        return elem;
    }
}