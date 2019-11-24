package kz.teamvictus.store.core.controller;

import io.swagger.annotations.*;
import kz.teamvictus.store.core.model.User;
import kz.teamvictus.store.core.service.IAvoService;
import kz.teamvictus.store.core.service.IKmeanService;
import kz.teamvictus.store.core.service.IMaxMinService;
import kz.teamvictus.store.core.service.IReduceService;
import kz.teamvictus.store.core.util.models.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Produces;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/algorithm")
@Api(tags = {"Algorithm"}, description = "C.R.U.D. operations for User", authorizations = {@Authorization(value = "bearerAuth")})
public class AlgorithmController {
    private static final Logger logger = LoggerFactory.getLogger(AlgorithmController.class);

    @Autowired
    private IKmeanService iKmeanService;
    @Autowired
    private IAvoService iAvoService;
    @Autowired
    private IMaxMinService iMaxMinService;
    @Autowired
    private IReduceService iReduceService;


    @GetMapping("/avo")
    @Produces("application/json")
    @ApiOperation(value = "AVO", tags = {"Algorithm"})
    public List<HashMap<String, Object>> getAVO() {
        logger.debug("inside AlgorithmController.getAVO() method");
        logger.debug("====================================================================");
        return iAvoService.start(null, 0);
    }

    @GetMapping("/kmean")
    @Produces("application/json")
    @ApiOperation(value = "K - Mean", tags = {"Algorithm"})
    public List<HashMap<String, Object>> getKmean() {
        logger.debug("inside AlgorithmController.getKmean() method");
        logger.debug("====================================================================");
        return iKmeanService.start(null, false);
    }

    @GetMapping("/max-min")
    @Produces("application/json")
    @ApiOperation(value = "MAX - MIN", tags = {"Algorithm"})
    public List<Data> getMaxMin() {
        logger.debug("inside AlgorithmController.getMaxMin() method");
        logger.debug("====================================================================");
        return iMaxMinService.start(false);
    }

    @GetMapping("/reduce")
    @Produces("application/json")
    @ApiOperation(value = "REDUCE", tags = {"Algorithm"})
    public List<HashMap<String, Object>> getReduce() {
        logger.debug("inside AlgorithmController.getReduce() method");
        logger.debug("====================================================================");
        return iReduceService.start(false, null, 7, 5);
    }
}
