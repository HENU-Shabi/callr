package xyz.luchengeng.callr

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import xyz.luchengeng.callr.bean.BaseHostPool

@Controller
internal class TestController @Autowired constructor(private val hostPool: BaseHostPool) {
    private val testDataCsv: String =
            "OS,OS_Event,genValue\n" +
                    "1858,0,5.507042253521127E-4\n" +
                    "2723,0,6.084507042253521E-4\n" +
                    "994,1,3.591549295774648E-4\n" +
                    "562,1,3.535211267605634E-4\n" +
                    "487,0,8.084507042253521E-4\n" +
                    "1183,0,4.788732394366198E-4\n" +
                    "1525,0,4.295774647887324E-4\n" +
                    "1029,0,4.0E-4\n" +
                    "253,0,4.323943661971831E-4\n" +
                    "1130,0,0.004666197183098591\n" +
                    "2428,0,3.774647887323944E-4\n" +
                    "889,0,0.0015352112676056337\n" +
                    "1091,0,3.929577464788733E-4\n" +
                    "1613,1,8.690140845070422E-4\n" +
                    "1105,1,5.47887323943662E-4\n" +
                    "528,0,3.9718309859154927E-4\n" +
                    "555,0,4.295774647887324E-4\n" +
                    "724,0,6.267605633802816E-4\n" +
                    "211,0,6.436619718309859E-4\n" +
                    "149,0,4.6478873239436623E-4\n" +
                    "1414,0,0.0062436619718309865\n" +
                    "709,0,0.005528169014084507\n" +
                    "309,0,5.929577464788733E-4\n" +
                    "1583,0,6.676056338028169E-4\n" +
                    "840,0,0.0010042253521126761\n" +
                    "391,1,4.492957746478873E-4\n" +
                    "2222,0,4.1549295774647886E-4\n" +
                    "423,1,5.450704225352113E-4\n" +
                    "922,1,3.873239436619718E-4\n" +
                    "3659,0,3.6760563380281694E-4\n" +
                    "2842,0,5.76056338028169E-4\n" +
                    "383,1,0.002319718309859155\n" +
                    "344,1,5.633802816901409E-4\n" +
                    "2673,0,4.2816901408450705E-4\n" +
                    "207,1,4.380281690140845E-4\n" +
                    "1424,0,9.605633802816901E-4\n" +
                    "628,0,5.52112676056338E-4\n" +
                    "365,0,3.957746478873239E-4\n" +
                    "1677,1,4.6619718309859153E-4\n" +
                    "1355,1,6.014084507042254E-4\n" +
                    "645,0,4.0E-4\n" +
                    "4673,0,8.225352112676056E-4\n" +
                    "1293,0,5.295774647887324E-4\n" +
                    "541,1,0.0058887323943661975\n" +
                    "488,0,5.450704225352113E-4\n" +
                    "1942,0,5.816901408450705E-4\n" +
                    "159,1,4.3943661971830984E-4\n" +
                    "365,1,4.859154929577465E-4\n" +
                    "470,0,8.183098591549295E-4\n" +
                    "552,1,4.1267605633802816E-4\n" +
                    "662,1,5.070422535211267E-4\n" +
                    "3897,0,7.070422535211268E-4\n" +
                    "1916,0,0.004630985915492957\n" +
                    "1924,0,6.323943661971831E-4\n" +
                    "830,0,0.005380281690140845\n" +
                    "490,1,4.492957746478873E-4\n" +
                    "3688,0,5.985915492957747E-4\n" +
                    "2782,0,0.002461971830985916\n" +
                    "639,0,4.197183098591549E-4\n" +
                    "3240,0,8.971830985915494E-4\n" +
                    "674,0,0.0014014084507042255\n" +
                    "670,0,7.23943661971831E-4\n" +
                    "1255,0,9.80281690140845E-4\n" +
                    "1204,1,5.71830985915493E-4\n" +
                    "2385,1,5.816901408450705E-4\n" +
                    "1541,0,6.647887323943662E-4\n" +
                    "383,0,3.380281690140845E-4\n" +
                    "1197,1,4.774647887323944E-4\n" +
                    "1589,0,8.971830985915494E-4\n" +
                    "1259,0,0.0012169014084507043\n" +
                    "551,1,0.0013690140845070422\n" +
                    "882,0,8.676056338028169E-4\n" +
                    "504,0,0.0054\n" +
                    "307,0,0.0013788732394366197\n" +
                    "1949,0,6.76056338028169E-4\n" +
                    "579,1,9.04225352112676E-4\n" +
                    "950,0,0.0047605633802816905\n" +
                    "1750,1,0.0019014084507042255\n" +
                    "125,1,6.028169014084506E-4\n" +
                    "2105,1,3.9718309859154927E-4\n"
    @RequestMapping(value = ["/test"],method = [RequestMethod.GET],produces = ["image/png"])
    fun test() : ResponseEntity<ByteArray> = ResponseEntity.ok(
        hostPool("/plot/GENE"){
            this.post(this@TestController.testDataCsv.toRequestBody("text/csv".toMediaType()))
        }.body!!.bytes())
}