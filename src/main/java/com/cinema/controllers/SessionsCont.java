package com.cinema.controllers;

import com.cinema.controllers.Main.Attributes;
import com.cinema.models.*;
import com.cinema.models.enums.Genre;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Objects;
import java.util.UUID;

@Controller
@RequestMapping("/sessions")
public class SessionsCont extends Attributes {

    @GetMapping("/{id}")
    public String session(@PathVariable Long id, Model model) {
        if (!repoSessions.existsById(id)) return "redirect:/catalog";
        model.addAttribute("s", repoSessions.getReferenceById(id));
        model.addAttribute("role", getRole());
        model.addAttribute("user", getUser());
        return "session";
    }

    @PostMapping("/comment/add/{id}")
    public String comment_add(@PathVariable Long id, @RequestParam String date, @RequestParam String comment) {
        Sessions session = repoSessions.getReferenceById(id);
        session.addComment(new Comments(getUser().getUsername(), date, comment));
        repoSessions.save(session);
        return "redirect:/sessions/{id}";
    }

    @PostMapping("/buy/{id}")
    public String buy(@PathVariable Long id, @RequestParam int count) {
        Sessions session = repoSessions.getReferenceById(id);

        Users user = getUser();
        user.addCart(new Carts(count, session.getIncome().getPrice(), (session.getIncome().getPrice() * count), session));
        repoUsers.save(user);

        session.setCount(session.getCount() - count);

        session.getIncome().setCount(session.getIncome().getCount() + count);
        session.getIncome().setIncome(session.getIncome().getIncome() + (count * session.getIncome().getPrice()));

        repoSessions.save(session);

        return "redirect:/sessions/{id}";
    }

    @GetMapping("/add/{id}")
    public String session_add(@PathVariable Long id, Model model) {
        model.addAttribute("director", repoDirectors.getReferenceById(id));
        model.addAttribute("role", getRole());
        model.addAttribute("genres", Genre.values());
        return "session_add";
    }

    @PostMapping("/add")
    public String session_add(Model model, @RequestParam long directorId, @RequestParam String name, @RequestParam MultipartFile poster, @RequestParam MultipartFile[] screenshots, @RequestParam String pub, @RequestParam String date, @RequestParam int year, @RequestParam int price, @RequestParam int count, @RequestParam Genre genre, @RequestParam String description) {
        try {
            String uuidFile = UUID.randomUUID().toString();
            String result_poster = "";
            if (poster != null && !Objects.requireNonNull(poster.getOriginalFilename()).isEmpty()) {
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdir();
                result_poster = uuidFile + "_" + poster.getOriginalFilename();
                poster.transferTo(new File(uploadPath + "/" + result_poster));
            }

            String[] result_screenshots = new String[0];
            if (screenshots != null && !Objects.requireNonNull(screenshots[0].getOriginalFilename()).isEmpty()) {
                uuidFile = UUID.randomUUID().toString();
                String result_screenshot;
                result_screenshots = new String[screenshots.length];
                for (int i = 0; i < result_screenshots.length; i++) {
                    result_screenshot = uuidFile + "_" + screenshots[i].getOriginalFilename();
                    screenshots[i].transferTo(new File(uploadPath + "/" + result_screenshot));
                    result_screenshots[i] = result_screenshot;
                }
            }

            Directors director = repoDirectors.getReferenceById(directorId);
            director.addSession(new Sessions(name, pub, description, date, result_poster, result_screenshots, year, price, count, genre));
            repoDirectors.save(director);

        } catch (Exception e) {
            model.addAttribute("author", repoDirectors.getReferenceById(directorId));
            model.addAttribute("role", getRole());
            model.addAttribute("message", "Некорректные данные!");
            model.addAttribute("genres", Genre.values());
            return "session_add";
        }
        return "redirect:/catalog/all";
    }

    @GetMapping("/edit/{id}")
    public String session_edit(@PathVariable(value = "id") Long id, Model model) {
        if (!repoSessions.existsById(id)) return "redirect:/catalog";
        model.addAttribute("s", repoSessions.getReferenceById(id));
        model.addAttribute("role", getRole());
        model.addAttribute("genres", Genre.values());
        return "session_edit";
    }

    @PostMapping("/edit/{id}")
    public String session_edit(@PathVariable Long id, Model model, @RequestParam String name, @RequestParam MultipartFile poster, @RequestParam MultipartFile[] screenshots, @RequestParam String pub, @RequestParam String date, @RequestParam int year, @RequestParam int price, @RequestParam int count, @RequestParam Genre genre, @RequestParam String description) {
        try {
            Sessions session = repoSessions.getReferenceById(id);

            session.setDescription(description);
            session.setName(name);
            session.setPub(pub);
            session.setCount(count);
            session.setYear(year);
            session.setDate(date);
            session.getIncome().setPrice(price);
            session.setGenre(genre);

            String uuidFile = UUID.randomUUID().toString();
            if (poster != null && !Objects.requireNonNull(poster.getOriginalFilename()).isEmpty()) {
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdir();
                String result_poster = uuidFile + "_" + poster.getOriginalFilename();
                poster.transferTo(new File(uploadPath + "/" + result_poster));
                session.setPoster(result_poster);
            }

            if (screenshots != null && !Objects.requireNonNull(screenshots[0].getOriginalFilename()).isEmpty()) {
                uuidFile = UUID.randomUUID().toString();
                String result_screenshot;
                String[] result_screenshots = new String[screenshots.length];
                for (int i = 0; i < result_screenshots.length; i++) {
                    result_screenshot = uuidFile + "_" + screenshots[i].getOriginalFilename();
                    screenshots[i].transferTo(new File(uploadPath + "/" + result_screenshot));
                    result_screenshots[i] = result_screenshot;
                }
                session.setScreenshots(result_screenshots);
            }
            repoSessions.save(session);
        } catch (Exception e) {
            model.addAttribute("session", repoSessions.getReferenceById(id));
            model.addAttribute("role", getRole());
            model.addAttribute("message", "Некорректные данные!");
            model.addAttribute("genres", Genre.values());
            return "session_edit";
        }
        return "redirect:/sessions/{id}/";
    }

    @GetMapping("/delete/{id}")
    public String session_delete(@PathVariable Long id) {
        repoSessions.deleteById(id);
        return "redirect:/catalog/all";
    }
}
