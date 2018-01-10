package models

import java.util.Date
import javax.inject.Inject

import anorm.SqlParser._
import anorm._
import play.api.db.DBApi

import scala.concurrent.Future

case class Todo (
									id: Option[Long] = None,
									label: String,
									todo_date: Option[Date]
								)

@javax.inject.Singleton
class TodoList @Inject()(dbapi: DBApi)(implicit ec: DatabaseExecutionContext) {

	private val db = dbapi.database("default")

	val todo = {
	  get[Option[Long]]("id") ~
	  get[String]("label") ~
		get[Option[Date]]("todo_date") map {
	    case id~label~todo_date => Todo(id, label, todo_date)
	  }
	}

  def all(): List[Todo] = db.withConnection { implicit c =>
	  SQL("select * from todo").as(todo *)
	}

	def create(todo: Todo) {
		db.withConnection { implicit c =>
			SQL("insert into todo (label, todo_date) values " +
					"({label}, {todo_date})").on(
					'label -> todo.label, 'todo_date -> todo.todo_date
			).executeUpdate()
		}
	}

	def delete(id: Long) {
		db.withConnection { implicit c =>
			SQL("delete from todo where id = {id}").on(
				'id -> id
			).executeUpdate()
		}
	}
  
}