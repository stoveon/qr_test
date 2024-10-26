package kr.co.qr.controller;

import com.google.gson.Gson;
import kr.co.qr.service.OcrV3ResultService;
import kr.co.qr.service.OcrV3StartService;
import kr.co.qr.service.QrMakeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final OcrV3StartService ocrV3StartService;

    private final QrMakeService qrMakeService;

    private final OcrV3ResultService ocrV3ResultService;

    private final Gson GSON = new Gson();

    @GetMapping(value = "/")
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView();
        System.out.println("HOME = START === ");
        mav.setViewName("main");
        return mav;
    }

    @RequestMapping(value = "/getStartApiUrl", method = RequestMethod.POST)
    public @ResponseBody String getStartApi(HttpServletRequest request) {

        log.info("[{}] {}", request.getMethod(), request.getRequestURL());

        String data = ocrV3StartService.sendStartApi();

        log.info("{}", GSON.toJson(data));

        return data;
    }

    @RequestMapping(value = "/qrOpen", method = {RequestMethod.POST})
    public ModelAndView qrOpen(HttpServletRequest request, HttpServletResponse response,
                               // --------------
                               @RequestParam(value = "token", defaultValue = "") String token,
                               // --------------
                               @RequestParam(value = "startUrl", defaultValue = "") String startUrl,
                               // --------------
                               @RequestParam(value = "resultUrl", defaultValue = "") String resultUrl
                               // --------------
    ) {
        log.info("{} >>> qrOpen !!! -> {}", token, startUrl);
        System.out.println(token + " >>> qrOpen !!! -> " + startUrl);
        ModelAndView mav = new ModelAndView();

        String qrImageString = qrMakeService.makeUrlToQrCode(startUrl);

        mav.setViewName("qr");

        mav.addObject("token", token);
        mav.addObject("qrImageString", qrImageString);
        mav.addObject("resultUrl", resultUrl);

        return mav;
    }

    @RequestMapping(value = "/sendResultApi", method = RequestMethod.POST)
    public @ResponseBody String sendResultApi(HttpServletRequest request,
                                              // --------------
                                              @RequestParam(value = "token", required = false, defaultValue = "") String token,
                                              // --------------
                                              @RequestParam(value = "resultUrl", required = false, defaultValue = "") String resultUrl
                                              // --------------
    ) {

        log.info("{} >>> [{}] {}", token, request.getMethod(), request.getRequestURL());
        System.out.println(token + " >>> sendResultApi = START +++ ");

        String data = ocrV3ResultService.sendResultApi(token, resultUrl);

        log.info("{} >>> {}", token, GSON.toJson(data));
        System.out.println(token + " >>> data = " + GSON.toJson(data));

        return data;
    }

}
