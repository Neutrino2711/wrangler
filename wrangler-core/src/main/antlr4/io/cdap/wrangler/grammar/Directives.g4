/*
 * Copyright © 2023 Cask Data, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

lexer grammar Directives;

// Add new token definitions for byte sizes and time durations
BYTE_SIZE: INT_PART ('.' DEC_PART)? BYTE_UNIT;

TIME_DURATION: INT_PART ('.' DEC_PART)? TIME_UNIT;

fragment BYTE_UNIT:
	[Bb] // byte
	| [Kk][Bb] // kilobyte
	| [Mm][Bb] // megabyte
	| [Gg][Bb] // gigabyte
	| [Tt][Bb]; // terabyte

fragment TIME_UNIT:
	'ns' // nanosecond
	| 'us' // microsecond
	| 'ms' // millisecond
	| 's' // second
	| 'm' // minute
	| 'h'; // hour

fragment INT_PART: '0' | [1-9] [0-9]*;

fragment DEC_PART: [0-9]+;