package com.parking.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.parking.entity.ParkingSpace;
import com.parking.service.IParkingSpaceService;
import com.parking.util.OperationLogHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/parking-space")
public class ParkingSpaceController {

    @Autowired
    private IParkingSpaceService parkingSpaceService;

    @Autowired
    private OperationLogHelper operationLogHelper;

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer size,
                       Model model) {
        PageHelper.startPage(page, size);
        List<ParkingSpace> list = parkingSpaceService.listAll();
        PageInfo<ParkingSpace> pageInfo = new PageInfo<>(list);
        model.addAttribute("page", pageInfo);
        return "parking-space-list";
    }

    @GetMapping("/add")
    public String addPage() {
        return "parking-space-add";
    }

    @PostMapping("/add")
    public String add(ParkingSpace space, HttpSession session) {
        parkingSpaceService.addSpace(space);
        operationLogHelper.log(session, "新增车位", "新增车位：" + space.getSpaceNumber());
        return "redirect:/parking-space/list";
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Long id, Model model) {
        ParkingSpace space = parkingSpaceService.getSpace(id);
        model.addAttribute("space", space);
        return "parking-space-edit";
    }

    @PostMapping("/update")
    public String update(ParkingSpace space, HttpSession session) {
        parkingSpaceService.updateSpace(space);
        operationLogHelper.log(session, "编辑车位", "编辑车位：" + space.getSpaceNumber());
        return "redirect:/parking-space/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, HttpSession session) {
        parkingSpaceService.deleteSpace(id);
        operationLogHelper.log(session, "删除车位", "删除车位ID：" + id);
        return "redirect:/parking-space/list";
    }
}