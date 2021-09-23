import chisel3.iotesters.PeekPokeTester
import chisel3.iotesters.Driver
import org.scalatest._

class HelloTest(dut:Hello) extends PeekPokeTester(dut) {
    step(1)
    step(1)
    step(1)
    step(1)
    step(1)
    step(1)
}
  // "Hello" should "pass" in {
  //   chisel3.iotesters.Driver(() => new Hello()) { c =>
  //     new PeekPokeTester(c) {

  //       var ledStatus = -1
  //       println("Start the blinking LED")
  //       for (i <- 0 until 100) {
  //         step(1)
  //         val ledNow = peek(c.io.led).toInt
  //         val s = if (ledNow == 0) "o" else "*"
  //         if (ledStatus != ledNow) {
  //           System.out.println(s)
  //           ledStatus = ledNow
  //         }
  //       }
  //       println("\nEnd the blinking LED")
  //     }
  //   } should be (true)
  // }

class HelloTestWave extends FlatSpec with Matchers {
  "Waveform" should "pass" in {
    Driver.execute(Array("--generate-vcd-output", "on"),() => new Hello()) 
        {c =>new HelloTest(c)
        } should be (true)
}
}