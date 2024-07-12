package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.example.UserInfo.*;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class Main {
    private static final String URI = "https://jsonplaceholder.typicode.com";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();


    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        User user = new User();
        Address address = new Address();
        Geo geo = new Geo();
        Company company = new Company();
        user.setId(9);
        user.setName("Leanne Graham");
        user.setUsername("Bret");
        user.setEmail("Sincere@april.biz");
        address.setStreet("Kulas Light");
        address.setSuite("Apt. 556");
        address.setCity("Gwenborough");
        address.setZipcode("92998-3874");
        geo.setLat("-37.3159");
        geo.setLng("81.1496");
        user.setAddress(address);
        user.setPhone("1-770-736-8031 x56442");
        user.setWebsite("hildegard.org");
        company.setName("Romaguera-Crona");
        company.setCatchPhrase("Multi-layered client-server neural-net");
        company.setBs("harness real-time e-markets");
        user.setCompany(company);


        createNewUser(user);
        changeUser(user);
        deleteUser(5);
        getAllUser();
        getUserById(10);
        getUserByUsername("Samantha");
        userLastComments(10);
        userOpenTasks(1);



    }

    public static void createNewUser(User user) throws IOException, InterruptedException, URISyntaxException {
        String userJson = gson.toJson(user);
        HttpRequest requestPostUser = HttpRequest.newBuilder(new URI(URI + "/users"))
                .POST(HttpRequest.BodyPublishers.ofString(userJson))
                .header("Content-type", "application/json")
                .build();

        HttpResponse<String> response = client.send(requestPostUser, HttpResponse.BodyHandlers.ofString());
        System.out.println("statusCode = " + response.statusCode());
        System.out.println(gson.toJson(gson.fromJson(response.body(), User.class)));

    }

    public static void changeUser(User user) throws URISyntaxException, IOException, InterruptedException {
        String userJson = gson.toJson(user);
        HttpRequest requestPutUser = HttpRequest.newBuilder(new URI(URI + "/users" + "/" + user.getId()))
                .PUT(HttpRequest.BodyPublishers.ofString(userJson))
                .header("Content-type", "application/json")
                .build();

        HttpResponse<String> response = client.send(requestPutUser, HttpResponse.BodyHandlers.ofString());
        System.out.println("statusCode " + response.statusCode());
        System.out.println(response.body());
    }

    public static void deleteUser(int id) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest requestDelete = HttpRequest.newBuilder(new URI(URI + "/users/" + id))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());
        System.out.println("response.statusCode() = " + response.statusCode());

    }

    public static void getAllUser() throws IOException, InterruptedException, URISyntaxException {
        HttpRequest requestGetAllUser = HttpRequest.newBuilder(new URI(URI + "/users"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(requestGetAllUser, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.statusCode());
        System.out.println(response.body());
    }

    public static void getUserById(int id) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest requestUserById = HttpRequest.newBuilder(new URI(URI + "/users/" + id))
                .GET()
                .build();
        HttpResponse<String> response = client.send(requestUserById, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.statusCode());
        System.out.println(response.body());
    }

    public static void getUserByUsername(String username) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest requestGetUserByUsername = HttpRequest.newBuilder(new URI(URI + "/users?username=" + username))
                .GET()
                .build();
        HttpResponse<String> response = client.send(requestGetUserByUsername, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.statusCode());
        System.out.println(response.body());

    }

    public static void userLastComments(int userId) throws URISyntaxException, IOException, InterruptedException {

        HttpRequest requestPosts = HttpRequest.newBuilder(new URI(URI + "/user/" + userId + "/posts"))
                .GET()
                .build();
        HttpResponse<String> responsePosts = client.send(requestPosts, HttpResponse.BodyHandlers.ofString());

        UserPosts[] userPosts = gson.fromJson(responsePosts.body(), UserPosts[].class);

        int userPostsId = userPosts[userPosts.length - 1].getId();

        HttpRequest requestComments = HttpRequest.newBuilder(new URI(URI + "/posts/" + userPostsId + "/comments"))
                .GET()
                .build();
        HttpResponse<String> responseComments = client.send(requestComments, HttpResponse.BodyHandlers.ofString());

        UserComments[] userComments = gson.fromJson(responseComments.body(), UserComments[].class);

        try (FileWriter writer = new FileWriter("user-" + userId + "-post-" + userPostsId + "-comments.json")) {
            gson.toJson(userComments, writer);
        } catch (IOException e) {
            System.out.println("e.getMessage() = " + e.getMessage());
        }
        System.out.println("responseComments.statusCode() = " + responseComments.statusCode());
        System.out.println("responsePosts.statusCode() = " + responsePosts.statusCode());
    }

    public static void userOpenTasks(int userid) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest requestGetUserTodos = HttpRequest.newBuilder(new URI(URI + "/user/" + userid + "/todos"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(requestGetUserTodos, HttpResponse.BodyHandlers.ofString());
        Type userTodosType = new TypeToken<List<UserTodos>>() {
        }.getType();
        List<UserTodos> userTodosAll = gson.fromJson(response.body(), userTodosType);

        List<UserTodos> userTodosListSorted = userTodosAll.stream()
                .filter(todo -> !todo.getCompleted())
                .toList();

        System.out.println("gson.toJson(userTodosListSorted) = " + gson.toJson(userTodosListSorted));


    }
}


