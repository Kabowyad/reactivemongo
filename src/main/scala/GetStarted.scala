import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.WriteResult

import scala.concurrent.{Await, ExecutionContext, Future}
import reactivemongo.bson.{BSONDocument, BSONDocumentReader}

import scala.concurrent.duration.Duration
import scala.util.Success

object GetStarted {
  case class Contacts(item: String, qty: Int)

  // My settings (see available connection options)
  import ExecutionContext.Implicits.global // use any appropriate context

  def insert(col: Future[BSONCollection]) = {
        val writeRes: WriteResult = Await.result(col.flatMap { x =>
          x.insert(BSONDocument(
            "item" -> "scala",
            "qty" -> 10
          ))
        }, Duration.Inf)
        println(s"s:$writeRes")
  }
  def main(args: Array[String]): Unit = {
    val driver1 = new reactivemongo.api.MongoDriver
    val connection3 = driver1.connection(List("localhost:27017"))
    val col: Future[BSONCollection] = connection3.database("mydb").map(_.collection("people"))
//    insert(col)

    val query = BSONDocument("item" -> "card")

    col.flatMap(_.find(BSONDocument("item" -> "card")).one[BSONDocument])

    val contacts:Future[Option[BSONDocument]] = col.flatMap(_.find(BSONDocument("item" -> "card")).one[BSONDocument])
    contacts.onComplete {
      case Success(value) => print("yolo {}", value.get.get("item"))
    }

    implicit object ContactsReader extends BSONDocumentReader[Contacts] {
      override def read(bson: BSONDocument): Contacts = {
        val opt: Option[Contacts] = for {
          item <- bson.getAs[String]("item")
          qty <- bson.getAs[Int]("qty").map(_.toInt)
        } yield new Contacts(item, qty)

        opt.get
      }
    }

    return
  }

}


//object Person {
//  implicit object PersonReader extends BSONDocumentReader[Person] {
//    def read(doc: BSONDocument): Person = {
//      val id = doc.getAs[BSONObjectID]("_id").get
//      val firstName = doc.getAs[String]("firstName").get
//      val lastName = doc.getAs[String]("lastName").get
//      val age = doc.getAs[Int]("age").get
//
//      Person(id, firstName, lastName, age)
//    }
//  }
//}
