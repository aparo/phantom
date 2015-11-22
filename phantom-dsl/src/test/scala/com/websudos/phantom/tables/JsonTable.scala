/*
 * Copyright 2013-2015 Websudos, Limited.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * - Explicit consent must be obtained from the copyright owner, Websudos Limited before any redistribution is made.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.websudos.phantom.tables

import com.websudos.phantom.builder.query.InsertQuery
import com.websudos.phantom.dsl._
import com.websudos.phantom.testkit._
import net.liftweb.json.{DefaultFormats, Extraction, JsonParser, compactRender}

case class JsonTest(prop1: String, prop2: String)

case class JsonClass(
  id: UUID,
  name: String,
  json: JsonTest,
  jsonOpt: Option[JsonTest],
  jsonList: List[JsonTest],
  jsonSet: Set[JsonTest]
)


class JsonTable extends CassandraTable[JsonTable, JsonClass] {

  implicit val formats = DefaultFormats

  object id extends UUIDColumn(this) with PartitionKey[UUID]

  object name extends StringColumn(this)

  object json extends JsonColumn[JsonTable, JsonClass, JsonTest](this) {
    override def fromJson(obj: String): JsonTest = {
      JsonParser.parse(obj).extract[JsonTest]
    }

    override def toJson(obj: JsonTest): String = {
      compactRender(Extraction.decompose(obj))
    }
  }

  object jsonOpt extends OptionalJsonColumn[JsonTable, JsonClass, JsonTest](this) {
    override def fromJson(obj: String): JsonTest = {
      JsonParser.parse(obj).extract[JsonTest]
    }

    override def toJson(obj: JsonTest): String = {
      compactRender(Extraction.decompose(obj))
    }
  }

  object jsonList extends JsonListColumn[JsonTable, JsonClass, JsonTest](this) {
    override def fromJson(obj: String): JsonTest = {
      JsonParser.parse(obj).extract[JsonTest]
    }

    override def toJson(obj: JsonTest): String = {
      compactRender(Extraction.decompose(obj))
    }
  }

  object jsonSet extends JsonSetColumn[JsonTable, JsonClass, JsonTest](this) {
    override def fromJson(obj: String): JsonTest = {
      JsonParser.parse(obj).extract[JsonTest]
    }

    override def toJson(obj: JsonTest): String = {
      compactRender(Extraction.decompose(obj))
    }
  }

  def fromRow(row: Row): JsonClass = {
    JsonClass(
      id(row),
      name(row),
      json(row),
      jsonOpt(row),
      jsonList(row),
      jsonSet(row)
    )
  }
}

object JsonTable extends JsonTable with PhantomCassandraConnector {
  def store(sample: JsonClass): InsertQuery.Default[JsonTable, JsonClass] = {
    insert
      .value(_.id, sample.id)
      .value(_.name, sample.name)
      .value(_.json, sample.json)
      .value(_.jsonOpt, sample.jsonOpt)
      .value(_.jsonList, sample.jsonList)
      .value(_.jsonSet, sample.jsonSet)
  }
}
