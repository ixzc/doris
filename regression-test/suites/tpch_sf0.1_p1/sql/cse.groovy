// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

// The cases is copied from https://github.com/trinodb/trino/tree/master
// /testing/trino-product-tests/src/main/resources/sql-tests/testcases/tpcds
// and modified by Doris.

suite('cse') {
    def q1 = """select s_suppkey,n_regionkey,(s_suppkey + n_regionkey) + 1 as x, (s_suppkey + n_regionkey) + 2 as y 
            from supplier join nation on s_nationkey=n_nationkey order by s_suppkey , n_regionkey limit 10 ;
            """

    def q2 = """select s_nationkey,s_suppkey ,(s_nationkey + s_suppkey), (s_nationkey + s_suppkey) + 1,  abs((s_nationkey + s_suppkey) + 1) 
    from supplier order by s_suppkey , s_suppkey limit 10 ;"""

    qt_cse "${q1}"

    explain {
        sql "${q1}"
        contains "intermediate projections:"
    }

    qt_cse_2 "${q2}"

    explain {
        sql "${q2}"
        multiContains("intermediate projections:", 2)
    }

    qt_cse_3 """ select sum(s_nationkey),sum(s_nationkey +1 ) ,sum(s_nationkey +2 )  , sum(s_nationkey + 3 ) from supplier ;"""

    qt_cse_4 """select sum(s_nationkey),sum(s_nationkey) + count(1) ,sum(s_nationkey) + 2 * count(1) , sum(s_nationkey)  + 3 * count(1) from supplier ;"""


}
