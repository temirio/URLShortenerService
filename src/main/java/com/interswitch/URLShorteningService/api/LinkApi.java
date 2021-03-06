package com.interswitch.URLShorteningService.api;

import com.interswitch.URLShorteningService.DAO.UrlDao;
import com.interswitch.URLShorteningService.services.ShortenLink;
import com.interswitch.URLShorteningService.api.model.SubmitRequest;
import com.interswitch.URLShorteningService.api.model.SubmitResponse;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("api")
public class LinkApi {


    private SubmitResponse getLinkResponse;
    //In memory cache HashMap for faster Access
    private Map<String,SubmitResponse> dictionary = new HashMap<String,SubmitResponse>();

    //Submit a Link to shorten. Generates a Link Id, collects an Object with only one Property {Url}
    @PostMapping("/submit")
    @ResponseBody
    public SubmitResponse submit(@RequestBody SubmitRequest submitRequest) throws Exception {
        String generatedCode = ShortenLink.generateShortUrl(5);
        AtomicBoolean isFound = new AtomicBoolean(false);

        for(int i = 0; i < dictionary.entrySet().size(); i++)
            for (SubmitResponse submitResponses : dictionary.values()) {
                if (submitResponses.getOldUrl().equals(submitRequest.getUrl())) {
                    getLinkResponse = new SubmitResponse("0", submitRequest.getUrl(), submitResponses.getLinkId(), "URL Already exists", null);
                    isFound.set(true);
                    return getLinkResponse;
                } else if (submitResponses.getOldUrl().equals(UrlDao.getOldurl(submitRequest.getUrl()).getOldUrl())) {
                    getLinkResponse = new SubmitResponse("0", UrlDao.getOldurl(submitRequest.getUrl()).getOldUrl(), submitResponses.getLinkId(), "URL Already exists", null);
                    isFound.set(true);
                    return getLinkResponse;
                }
            }

        if (isFound.get() == false) {
            getLinkResponse = new SubmitResponse(generatedCode, submitRequest.getUrl(), "200", "URL created Successfully", null);
            dictionary.put(generatedCode, getLinkResponse);
            UrlDao.submitLink(getLinkResponse);
        }

        return getLinkResponse;
    }

    //Find a link by LinkId
    @GetMapping("/getlink/{KeyId}")
    @ResponseBody
    public SubmitResponse getLink(@PathVariable("KeyId") String Id) throws Exception {

        if(dictionary.containsKey(Id)){
            return dictionary.get(Id);
        }
        else if(!dictionary.containsKey(Id)) {
            return UrlDao.getLink(Id);
        }
        else{
            return getLinkResponse = new SubmitResponse("0", null ,"404","Not Found",null);
        }

    }

    //Return all Shortened Links
    @GetMapping("/getall")
    public  Map<String,SubmitResponse> getUrls () throws Exception {
        if (dictionary != null){
            return dictionary;
        }else{
            return UrlDao.getall();
        }
    }

}
