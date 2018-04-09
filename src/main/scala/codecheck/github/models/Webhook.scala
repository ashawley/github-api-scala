package codecheck.github.models

import scala.collection.immutable.Iterable
import scala.collection.immutable.Seq
import org.json4s.JValue

class Webhook(value: JValue) extends AbstractJson(value) {
  def id: Long = get("id").toLong
  def url = get("url")
  def test_url = get("test_url")
  def ping_url = get("ping_url")
  def name = get("name")
  def events = seq("events")
  def active = boolean("active")
  def config = WebhookConfig(opt("config.url"), opt("config.content_type"), opt("config.secret"), opt("config.insecure_ssl").map(_ == "1"));
  def last_response = WebhookResponse(value \ "last_response")
  def updated_at = getDate("updated_at")
  def created_at = getDate("created_at")
}

case class WebhookConfig(
  url: Option[String],
  content_type: Option[String],
  secret: Option[String],
  insecure_ssl: Option[Boolean]
) extends AbstractInput

object WebhookConfig {
  def apply(
    url: String,
    content_type: String = "json",
    secret: Option[String] = None,
    insecure_ssl: Boolean = false
  ): WebhookConfig = WebhookConfig(Some(url), Some(content_type), secret, Some(insecure_ssl))
}

case class WebhookCreateInput(
  name: String,
  config: WebhookConfig,
  active: Boolean = true,
  events: Iterable[String] = Seq("push"),
  add_events: Iterable[String] = Seq.empty[String],
  remove_events: Iterable[String] = Seq.empty[String]
  ) extends AbstractInput

case class WebhookUpdateInput(
  config: Option[WebhookConfig] = None,
  events: Option[Iterable[String]] = None,
  add_events: Option[Iterable[String]] = None,
  remove_events: Option[Iterable[String]] = None,
  active: Option[Boolean] = None
  ) extends AbstractInput

case class WebhookResponse(value: JValue) extends AbstractJson(value) {
  def code = get("code")
  def status = get("status")
  def message = get("message")
}
