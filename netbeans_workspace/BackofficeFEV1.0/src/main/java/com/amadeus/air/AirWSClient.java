package com.amadeus.air;

import com.ets.fe.util.RestClientUtil;
import com.ets.fe.APIConfig;

/**
 *
 * @author Yusuf
 */
public class AirWSClient {


    public Integer postAir(AIR air){
        Integer httpStatus = RestClientUtil.postAIR(APIConfig.get("ws.air.air"),air);
        return httpStatus;
    }   
}
