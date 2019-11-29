package kz.teamvictus.store.core.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import kz.teamvictus.store.core.service.*;
import kz.teamvictus.store.core.util.models.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Produces;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/algorithm-syntethis")
@Api(tags = {"AlgorithmSynthesis"}, description = "C.R.U.D. operations for AlgorithmSynthesis", authorizations = {@Authorization(value = "bearerAuth")})
public class AlgorithmSynthesisController {
    private static final Logger logger = LoggerFactory.getLogger(AlgorithmSynthesisController.class);

    @Autowired
    private ISynthesisService iSynthesisService;



    @GetMapping("/{sourceFileName}/{delta}")
    @Produces("application/json")
    @ApiOperation(value = "Alg Synthesis", tags = {"AlgorithmSynthesis"})
    public HashMap getAlgSynthesis(@PathVariable("sourceFileName")  String sourceFileName,
                                   @PathVariable("delta")           Integer delta) {
        logger.debug("inside AlgorithmController.getAlgSynthesis() method");
        logger.debug("====================================================================");
        return iSynthesisService.start(sourceFileName, delta);
    }

}
