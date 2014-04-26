package controllers;

import akka.actor.ActorRef;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.core.JsonProcessingException;
import models.Exercise;
import play.api.templates.Xml;
import play.cache.Cache;
    import play.cache.Cached;
import play.data.Form;
import play.db.ebean.Model;
import play.db.ebean.Transactional;
import play.libs.Akka;
import play.libs.F;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import scala.collection.mutable.StringBuilder;

import java.util.ArrayList;
import java.util.List;

import static play.data.Form.form;

public class FitnessController extends Controller {
    public static Result welcome() {
        return ok("Welcome to Borg Fitness! Time to assimilate into fitness!");
    }

    public static Result welcomeWithName(String name) {
        return ok(String.format("Welcome to Borg Fitness %s! Time to assimilate into fitness!", name));
    }

    public static Result exerciseOfTheDay() {
        return ok(views.html.exerciseoftheday.render(new Exercise("Swimming", 60)));
    }

    public static Result workoutOfTheDay() {
        List<Exercise> exercises = new ArrayList<Exercise>();
        exercises.add(new Exercise("Running Sprints", 10));
        exercises.add(new Exercise("Running Light Jog", 20));
        exercises.add(new Exercise("Running Sprints", 10));
        exercises.add(new Exercise("Cool Down", 10));
        return ok(views.html.workoutoftheday.render(exercises));
    }

    public static Result initExercise() {
        Form<Exercise> exerciseForm = form(Exercise.class);
        return ok(views.html.createexercise.render(exerciseForm));
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public static Result createExercise() {
        Form<Exercise> filledInForm = form(Exercise.class).bindFromRequest();
        if (filledInForm.hasErrors()) {
            return badRequest(views.html.createexercise.render(filledInForm));
        }
        Exercise exercise = filledInForm.get();
        Ebean.save(exercise);
        System.out.println(Cache.get("channels"));
        for (WebSocket.Out out : (List<WebSocket.Out>) Cache.get("channels")) {
            out.write("> Added exercise! " + exercise);
        }

        ActorRef actorRef = Akka.system().actorFor("/user/receiverActor");
        actorRef.tell("Sending a message to another core!", null);
        actorRef.tell("Send", null);
        Cache.remove("exercise-list");
        flash("flash_status", "Exercise Created Successfully");
        return getList();
    }

    @Cached(key = "exercise-list", duration = 60)
    @SuppressWarnings("unchecked")
    public static Result getList() {
        Model.Finder finder = new Model.Finder<Long, Exercise>(Long.class, Exercise.class);
        return ok(views.html.allexercises.render((List<Exercise>) finder.all()));
    }

    @SuppressWarnings("unchecked")
    public static Result getJsonList() throws JsonProcessingException {
        Model.Finder finder = new Model.Finder<Long, Exercise>(Long.class, Exercise.class);
        return ok(Json.toJson((List<Exercise>) finder.all()));
    }

    @SuppressWarnings("unchecked")
    public static Result getXMLList() {
        Model.Finder finder = new Model.Finder<Long, Exercise>(Long.class, Exercise.class);
        StringBuilder sb = new StringBuilder();
        sb.append("<exercises>\n");
        for (Exercise exercise : (List<Exercise>) finder.all()) {
            sb.append(String.format("<exercise name=\"%s\" minutes=\"%d\" />\n", exercise.getName(), exercise.getMinutes()));
        }
        sb.append("</exercises>");
        return ok(new Xml(sb));
    }

    public static WebSocket<String> wsCall() {
        return new WebSocket<String>() {
            @SuppressWarnings("unchecked")
            public void onReady(final WebSocket.In<String> in,
                                final WebSocket.Out<String> out) {

                if (Cache.get("channels") == null) {
                    List<Out> outs = new ArrayList<Out>();
                    outs.add(out);
                    Cache.set("channels", outs);
                } else ((List<Out>) Cache.get("channels")).add(out);
                System.out.println("<" + Cache.get("channels"));
                in.onClose(new F.Callback0() {
                    @Override
                    public void invoke() throws Throwable {
                        ((List<Out>) Cache.get("channels")).remove(out);
                        out.close();
                    }
                });
            }
        };
    }
}
