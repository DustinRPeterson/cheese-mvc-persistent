package org.launchcode.controllers;


import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value="menu")
public class MenuController {

    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private MenuDao menuDao;

    @RequestMapping(value="")
    public String index(Model model) {

        model.addAttribute("menu", menuDao.findAll());
        model.addAttribute("title", "Menu");

        return "menu/index";
    }

    @RequestMapping(value="add", method= RequestMethod.GET)
    public String displayAddMenuForm(Model model) {
        model.addAttribute("title", "Add menu item");
        model.addAttribute(new Menu());

        return "menu/add";

    }

    @RequestMapping(value="add", method=RequestMethod.POST)
    public String processAddMenuForm(Model model, @ModelAttribute @Valid Menu menu, Errors errors) {



        if (errors.hasErrors()) {
            model.addAttribute("title", "New Category");
            return "menu/add";
        }

        menuDao.save(menu);
        return "redirect:view/" + menu.getId();
    }

    @RequestMapping(path="add-item/{id}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable("id") int id) {
        Menu menuItem = menuDao.findOne(id);
        //model.addAttribute("menu", menuDao.findOne(id));
        model.addAttribute("title", "Add item to menu: " + menuItem.getName());
        List<Cheese> cheeseList = new ArrayList<>();
        List<Cheese> menuList = new ArrayList<>();


        cheeseList = menuItem.getCheeses();

        for(Cheese cheese: cheeseDao.findAll()) {
            if(!cheeseList.contains(cheese)) {
                menuList.add(cheese);
            }
        }

        //for(Cheese cheese : cheeseList) {
        //    menuList.remove(cheese);
        //}
        model.addAttribute("cheeses", menuList);
        model.addAttribute(new AddMenuItemForm());

        return "/menu/add-item";


    }

    @RequestMapping(path="add-item/{id}", method=RequestMethod.POST)
    public String addItem(Model model, @PathVariable("id") int id,  @Valid AddMenuItemForm addMenuItemForm, Errors errors) {

        if(errors.hasErrors()) {
            model.addAttribute("errors", errors);
            return "add-item";
        }

        Cheese cheeseToAdd = cheeseDao.findOne(addMenuItemForm.getCheeseId());
        Menu menuToAdd = menuDao.findOne(id);
        cheeseToAdd.addItem(menuToAdd);
        menuToAdd.addItem(cheeseToAdd);
        menuDao.save(menuToAdd);

        return "redirect:../view/" + menuToAdd.getId();
    }


    @RequestMapping(path="view/{id}")
    public String viewMenu(Model model, @PathVariable("id") int id) {
        Menu thisMenu = menuDao.findOne(id);
        model.addAttribute("title", "Menu: " + thisMenu.getName());
        model.addAttribute("menu", thisMenu);
        return"/menu/view";

    }
}
