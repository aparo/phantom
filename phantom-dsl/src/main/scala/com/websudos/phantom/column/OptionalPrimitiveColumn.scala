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
package com.websudos.phantom.column

import com.websudos.phantom.builder.syntax.CQLSyntax

import scala.annotation.implicitNotFound
import com.datastax.driver.core.Row
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.builder.primitives.Primitive
import com.websudos.phantom.builder.query.CQLQuery

import scala.util.Try


@implicitNotFound(msg = "Type ${T} must be a Cassandra primitive")
class OptionalPrimitiveColumn[Owner <: CassandraTable[Owner, Record], Record, @specialized(Int, Double, Float, Long, Boolean,
  Short) T : Primitive](t: CassandraTable[Owner, Record]) extends OptionalColumn[Owner, Record, T](t) {

  def cassandraType: String = Primitive[T].cassandraType

  def optional(r: Row): Try[T] = implicitly[Primitive[T]].fromRow(name, r)

  override def qb: CQLQuery = {
    val root = CQLQuery(name).forcePad.append(cassandraType)
    if (isStaticColumn) {
      root.forcePad.append(CQLSyntax.static)
    } else {
      root
    }
  }

  override def asCql(v: Option[T]): String = v.map(Primitive[T].asCql).getOrElse(null.asInstanceOf[String])
}
