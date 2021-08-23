package duke;

import duke.task.*;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class Storage {
    private final String filePath;

    /**
     * Initialises Storage with a filePath for the dataFile.
     *
     * @param filePath the relative filePath for the dataFile.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads data from the dataFile.
     * If there is no dataFile, creates one.
     *
     * @return the ArrayList of tasks obtained from the dataFile.
     * @throws DukeException is thrown when there is an error in creating the dataFile.
     */
    public ArrayList<Task> loadData() throws DukeException {
        File folder = new File("./data/");
        if (folder.mkdir()) {
            System.out.println("A data folder is created.");
        }
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            File dataFile = new File(filePath);
            if (dataFile.createNewFile()) {
                System.out.println("A data file is created for saving your data.");
            } else {
                System.out.println("Loading your previous data...");
                Scanner sc = new Scanner(dataFile);
                while (sc.hasNextLine()) {
                    String taskString = sc.nextLine();
                    tasks.add(stringToTask(taskString));
                }
                sc.close();
                System.out.println("Successfully loaded your data!");
            }
        } catch (IOException e) {
            throw new DukeException("There is an error in creating the data file.");
        }
        return tasks;
    }

    /**
     * Saves data to the dataFile.
     */
    public void saveData(TaskList taskList) throws DukeException {
        try {
            FileWriter writer = new FileWriter(filePath);
            for (Task t : taskList.getTasks()) {
                writer.write(t.toString() + "\n");
            }
            writer.close();
            System.out.println("Task saved!");
        } catch (IOException e) {
            throw new DukeException("There is an error in saving the data.");
        }
    }

    /**
     * Converts the String of a task into a Task.
     * For reading saved data from the dataFile.
     *
     * @param taskString the String of a task, taken from the data file.
     * @return the Task as described by the taskString.
     */
    private Task stringToTask(String taskString) {
        Task result = new Task();
        String taskType = taskString.substring(0, 3);
        String taskStatus = taskString.substring(3, 6);
        String taskDetail = taskString.substring(7);
        switch (taskType) {
        case ("[T]"):
            result = new Todo(taskDetail);
            if (taskStatus.equals("[X]")) {
                result.setDone();
            }
            return result;
        case ("[D]"):
            String[] deadlineDescription = taskDetail
                    .substring(0, taskDetail.length() - 1)
                    .split("by: ", 2);
            String[] dateTimeOfDeadline = deadlineDescription[1].split(", ", 2);
            if (dateTimeOfDeadline.length == 1) {
                result = new Deadline(deadlineDescription[0]
                        .substring(0, deadlineDescription[0].length() - 2),
                        LocalDate.parse(dateTimeOfDeadline[0], DateTimeFormatter.ofPattern("dd MMM yyyy")));
            } else {
                result = new Deadline(deadlineDescription[0]
                        .substring(0, deadlineDescription[0].length() - 2),
                        LocalDate.parse(dateTimeOfDeadline[0], DateTimeFormatter.ofPattern("dd MMM yyyy")),
                        LocalTime.parse(dateTimeOfDeadline[1]));
            }
            if (taskStatus.equals("[X]")) {
                result.setDone();
            }
            return result;
        case ("[E]"):
            String[] eventDescription = taskDetail
                    .substring(0, taskDetail.length() - 1)
                    .split("at: ", 2);
            String[] dateTimeOfEvent = eventDescription[1].split(", ", 2);
            if (dateTimeOfEvent.length == 1) {
                result = new Event(eventDescription[0]
                        .substring(0, eventDescription[0].length() - 2),
                        LocalDate.parse(dateTimeOfEvent[0], DateTimeFormatter.ofPattern("dd MMM yyyy")));
            } else {
                result = new Deadline(eventDescription[0]
                        .substring(0, eventDescription[0].length() - 2),
                        LocalDate.parse(dateTimeOfEvent[0], DateTimeFormatter.ofPattern("dd MMM yyyy")),
                        LocalTime.parse(dateTimeOfEvent[1]));
            }
            if (taskStatus.equals("[X]")) {
                result.setDone();
            }
            return result;
        default:
            return result;
        }
    }
}
