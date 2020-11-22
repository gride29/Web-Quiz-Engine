package engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;

@RestController
class QuizController {

    private final QuizRepository quizRepository;

    @Autowired
    public QuizController(QuizRepository quizRepository, UserService userService, UserRepository userRepository) {
        this.quizRepository = quizRepository;
    }

    @GetMapping(path = "/api/quizzes/{id}")
    public Quiz getQuiz(@PathVariable("id") int id) {
        return quizRepository.findById(id)
                .orElseThrow(QuizNotFoundException::new);
    }

    Page<Quiz> getAllQuizzes(int pageNo, int pageSize) {
        Pageable paging = PageRequest.of(pageNo,pageSize);
        return quizRepository.findAll(paging);
    }

    @GetMapping(path = "/api/quizzes")
    public @ResponseBody ResponseEntity<Page<Quiz>> getQuizzes (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int pageSize
    ){
        return new ResponseEntity<Page<Quiz>>(
                getAllQuizzes(page,pageSize),
                HttpStatus.OK
        );
    }

    @PostMapping(value = "/api/quizzes", consumes = "application/json")
    @ResponseBody
    public Quiz createQuiz(@Valid @RequestBody Quiz quiz) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        quiz.setUser(user);
        return quizRepository.save(quiz);
    }

    @PostMapping(path = "/api/quizzes/{id}/solve")
    @ResponseBody
    public Feedback solveQuiz(@PathVariable("id") int id, @RequestBody() HashMap<String, int[]> answer) {
        Quiz quiz = quizRepository.findById(id).orElseThrow(QuizNotFoundException::new);
        int[] quizAns;
        int[] arr1 = Arrays.copyOf(answer.get("answer"), answer.get("answer").length);
        if (quiz.getAnswer() == null) {
            quizAns = new int[]{};
        } else {
            quizAns = Arrays.copyOf(quiz.getAnswer(), quiz.getAnswer().length);
        }
        Arrays.sort(arr1);
        Arrays.sort(quizAns);
        if (Arrays.equals(arr1, quizAns)) {
            return new Feedback(true, "Congratulations, you're right!");
        } else {
            return new Feedback(false, "Wrong answer! Please, try again.");
        }
    }

    @DeleteMapping(path = "/api/quizzes/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteQuiz(@PathVariable("id") int id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No quiz with id: " + id
                ));
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found.");
        }
        if (quiz.getUser().getId() != user.getId()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        quizRepository.delete(quiz);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
