package kz.teamvictus.store.core.controller;

import io.swagger.annotations.*;
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

    @GetMapping("/avo/{clusterCount}/{gammaParam}")
    @Produces("application/json")
    @ApiOperation(value = "AVO", tags = {"Algorithm"})
    public HashMap getAVO(@PathVariable("clusterCount") Integer clusterCount,
                          @PathVariable("gammaParam") Integer gammaParam) {
        logger.debug("inside AlgorithmController.getAVO() method");
        logger.debug("====================================================================");
        HashMap map = new HashMap<>();
        List<HashMap<String, Object>> cluster = iAvoService.start(null, clusterCount, gammaParam);
        map.put("ALG", cluster);
        map.put("FQ" , iReduceService.getQualityFunctional(cluster));
        return map;
    }

    @GetMapping("/kmean/{clusterCount}/{iterCount}/{viaNearestNeighbor}")
    @Produces("application/json")
    @ApiOperation(value = "K - Mean", tags = {"Algorithm"})
    public HashMap getKmean(@PathVariable("clusterCount")       Integer clusterCount,
                            @PathVariable("iterCount")          Integer iterCount,
                            @PathVariable("viaNearestNeighbor") Boolean viaNearestNeighbor) {
        logger.debug("inside AlgorithmController.getKmean() method");
        logger.debug("====================================================================");
        HashMap map = new HashMap<>();
        List<HashMap<String, Object>> cluster = iKmeanService.start(null, viaNearestNeighbor, clusterCount, iterCount);
        map.put("ALG", cluster);
        map.put("FQ" , iReduceService.getQualityFunctional(cluster));

        return map;
    }

    @GetMapping("/max-min/{clusterCount}/{iterCount}/{viaNearestNeighbor}/{viaMatrixDistance}")
    @Produces("application/json")
    @ApiOperation(value = "MAX - MIN", tags = {"Algorithm"})
    public HashMap getMaxMin(@PathVariable("clusterCount")       Integer clusterCount,
                             @PathVariable("iterCount")          Integer iterCount,
                             @PathVariable("viaNearestNeighbor") Boolean viaNearestNeighbor,
                             @PathVariable("viaMatrixDistance")  Boolean viaMatrixDistance) {
        logger.debug("inside AlgorithmController.getMaxMin() method");
        logger.debug("====================================================================");

        HashMap map = new HashMap<>();
        List<Data> zeroList = iMaxMinService.start(viaMatrixDistance);
        List<HashMap<String, Object>> cluster = iKmeanService.start(zeroList, viaNearestNeighbor, clusterCount, iterCount);
        map.put("ALG", cluster);
        map.put("FQ" , iReduceService.getQualityFunctional(cluster));
        return map;
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
