import akka.actor.ActorSystem
import akka.actor.Actor
import com.typesafe.config.ConfigFactory
import akka.actor.ActorRef
import akka.actor.Props
import naogateway.value.NaoMessages._
import naogateway.value.NaoMessages.Conversions._
import naogateway.value.NaoVisionMessages._
import recognition.Converter

object RemoteTest extends App {
  val config = ConfigFactory.load()
  val system = ActorSystem("remoting", config.getConfig("remoting").withFallback(config))

  val naoActor = system.actorFor("akka://naogateway@192.168.1.100:2550/user/hanna")
  system.actorOf(Props[MyResponseTestActor])

  class MyResponseTestActor extends Actor {
    override def preStart = naoActor ! Connect

    def receive = {
      case (response: ActorRef, noResponse: ActorRef, vision: ActorRef) => {
        trace(response)
        trace(noResponse)
        trace(vision)

        val info = recognition.Google.recognize("recordings/01.wav")

        for (m <- Converter.convert(info.text)) noResponse ! m

      }
      case x => trace(x)
    }

    def trace(a: Any) = log.info(a.toString)
    def error(a: Any) = log.warning(a.toString)
    def wrongMessage(a: Any, state: String) = log.warning("wrong message: " + a + " in " + state)
    import akka.event.Logging
    val log = Logging(context.system, this)

  }
  Thread.sleep(2000)
  system.shutdown
}