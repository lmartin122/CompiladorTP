//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";

//#line 2 "grammer.y"
package Sintactico;

import Lexico.AnalizadorLexico;
import java.util.Scanner;
import Tools.Logger;
import java.util.ArrayList;
import java.io.File;
import Tools.Tupla;
import Tools.TablaSimbolos;
import java.io.IOException;
//#line 27 "Parser.java"

public class Parser {

  boolean yydebug; // do I want debug output?
  int yynerrs; // number of errors so far
  int yyerrflag; // was there an error?
  int yychar; // the current working character

  // ########## MESSAGES ##########
  // ###############################################################
  // method: debug
  // ###############################################################
  void debug(String msg) {
    if (yydebug)
      System.out.println(msg);
  }

  // ########## STATE STACK ##########
  final static int YYSTACKSIZE = 500; // maximum stack size
  int statestk[] = new int[YYSTACKSIZE]; // state stack
  int stateptr;
  int stateptrmax; // highest index of stackptr
  int statemax; // state when highest index reached
  // ###############################################################
  // methods: state stack push,pop,drop,peek
  // ###############################################################

  final void state_push(int state) {
    try {
      stateptr++;
      statestk[stateptr] = state;
    } catch (ArrayIndexOutOfBoundsException e) {
      int oldsize = statestk.length;
      int newsize = oldsize * 2;
      int[] newstack = new int[newsize];
      System.arraycopy(statestk, 0, newstack, 0, oldsize);
      statestk = newstack;
      statestk[stateptr] = state;
    }
  }

  final int state_pop() {
    return statestk[stateptr--];
  }

  final void state_drop(int cnt) {
    stateptr -= cnt;
  }

  final int state_peek(int relative) {
    return statestk[stateptr - relative];
  }

  // ###############################################################
  // method: init_stacks : allocate and prepare stacks
  // ###############################################################
  final boolean init_stacks() {
    stateptr = -1;
    val_init();
    return true;
  }

  // ###############################################################
  // method: dump_stacks : show n levels of the stacks
  // ###############################################################
  void dump_stacks(int count) {
    int i;
    System.out.println("=index==state====value=     s:" + stateptr + "  v:" + valptr);
    for (i = 0; i < count; i++)
      System.out.println(" " + i + "    " + statestk[i] + "      " + valstk[i]);
    System.out.println("======================");
  }

  // ########## SEMANTIC VALUES ##########
  // public class ParserVal is defined in ParserVal.java

  String yytext;// user variable to return contextual strings
  ParserVal yyval; // used to return semantic vals from action routines
  ParserVal yylval;// the 'lval' (result) I got from yylex()
  ParserVal valstk[];
  int valptr;

  // ###############################################################
  // methods: value stack push,pop,drop,peek.
  // ###############################################################
  void val_init() {
    valstk = new ParserVal[YYSTACKSIZE];
    yyval = new ParserVal();
    yylval = new ParserVal();
    valptr = -1;
  }

  void val_push(ParserVal val) {
    if (valptr >= YYSTACKSIZE)
      return;
    valstk[++valptr] = val;
  }

  ParserVal val_pop() {
    if (valptr < 0)
      return new ParserVal();
    return valstk[valptr--];
  }

  void val_drop(int cnt) {
    int ptr;
    ptr = valptr - cnt;
    if (ptr < 0)
      return;
    valptr = ptr;
  }

  ParserVal val_peek(int relative) {
    int ptr;
    ptr = valptr - relative;
    if (ptr < 0)
      return new ParserVal();
    return valstk[ptr];
  }

  final ParserVal dup_yyval(ParserVal val) {
    ParserVal dup = new ParserVal();
    dup.ival = val.ival;
    dup.dval = val.dval;
    dup.sval = val.sval;
    dup.obj = val.obj;
    return dup;
  }

  // #### end semantic value section ####
  public final static short CLASS = 257;
  public final static short INTERFACE = 258;
  public final static short IMPLEMENT = 259;
  public final static short RETURN = 260;
  public final static short IF = 261;
  public final static short ELSE = 262;
  public final static short END_IF = 263;
  public final static short FOR = 264;
  public final static short IN = 265;
  public final static short RANGE = 266;
  public final static short IMPL = 267;
  public final static short PRINT = 268;
  public final static short TOD = 269;
  public final static short EQUAL_OPERATOR = 270;
  public final static short NOT_EQUAL_OPERATOR = 271;
  public final static short GREATER_THAN_OR_EQUAL_OPERATOR = 272;
  public final static short LESS_THAN_OR_EQUAL_OPERATOR = 273;
  public final static short MINUS_ASSIGN = 274;
  public final static short VOID = 275;
  public final static short LONG = 276;
  public final static short UINT = 277;
  public final static short DOUBLE = 278;
  public final static short CADENA = 279;
  public final static short ID = 280;
  public final static short CTE_DOUBLE = 281;
  public final static short CTE_UINT = 282;
  public final static short CTE_LONG = 283;
  public final static short YYERRCODE = 256;
  final static short yylhs[] = { -1,
      0, 1, 1, 2, 2, 2, 2, 3, 3, 3,
      7, 9, 9, 10, 11, 11, 12, 15, 15, 16,
      16, 17, 18, 13, 20, 22, 23, 23, 23, 23,
      23, 21, 21, 24, 26, 8, 27, 27, 4, 4,
      4, 29, 29, 29, 29, 30, 30, 31, 5, 5,
      5, 5, 33, 34, 34, 35, 36, 37, 38, 39,
      41, 42, 42, 42, 43, 43, 43, 43, 43, 19,
      44, 44, 44, 45, 45, 45, 45, 45, 46, 46,
      47, 47, 48, 48, 48, 48, 48, 48, 40, 40,
      49, 49, 49, 14, 14, 50, 32, 51, 51, 52,
      52, 53, 28, 25, 25, 25, 25, 55, 55, 54,
      54, 56, 56, 6, 6, 57, 57, 57, 57, 57,
      57, 58, 66, 59, 59, 59, 59, 59, 59, 67,
      67, 67, 64, 68, 68, 65, 60, 60, 60, 60,
      60, 60, 60, 61, 61, 61, 61, 61, 61, 61,
      61, 61, 61, 62, 62, 62, 62, 62, 69, 70,
      72, 71, 63, 63, 63,
  };
  final static short yylen[] = { 2,
      1, 1, 2, 1, 1, 1, 1, 3, 4, 3,
      3, 1, 2, 1, 1, 1, 3, 1, 3, 1,
      3, 1, 1, 2, 2, 1, 4, 4, 6, 3,
      3, 1, 1, 2, 1, 2, 1, 3, 3, 4,
      3, 3, 3, 3, 2, 1, 2, 2, 5, 6,
      6, 4, 3, 1, 2, 1, 2, 1, 3, 1,
      1, 1, 3, 3, 1, 3, 3, 3, 3, 1,
      1, 3, 3, 1, 4, 3, 3, 3, 1, 1,
      1, 3, 1, 1, 1, 2, 2, 2, 1, 1,
      5, 4, 7, 1, 1, 1, 1, 1, 1, 1,
      1, 1, 1, 5, 3, 4, 2, 3, 2, 1,
      2, 1, 2, 1, 1, 1, 1, 1, 1, 1,
      1, 2, 2, 1, 1, 1, 1, 1, 1, 1,
      1, 1, 1, 1, 1, 1, 7, 7, 6, 6,
      7, 7, 7, 9, 9, 9, 9, 9, 9, 9,
      9, 9, 8, 11, 11, 11, 9, 9, 1, 1,
      1, 1, 3, 3, 3,
  };
  final static short yydefred[] = { 0,
      0, 0, 0, 0, 0, 0, 0, 26, 101, 100,
      102, 0, 0, 136, 0, 0, 2, 4, 5, 6,
      7, 128, 0, 0, 0, 130, 95, 134, 0, 135,
      94, 96, 98, 99, 114, 115, 125, 126, 127, 129,
      132, 131, 0, 124, 133, 0, 0, 0, 0, 0,
      0, 159, 0, 0, 0, 0, 0, 0, 0, 107,
      110, 0, 3, 22, 0, 18, 0, 33, 24, 32,
      0, 25, 90, 89, 0, 122, 165, 0, 0, 10,
      41, 0, 0, 0, 8, 0, 0, 0, 39, 0,
      0, 80, 83, 84, 85, 0, 0, 0, 0, 0,
      0, 0, 74, 79, 81, 0, 0, 97, 0, 164,
      163, 0, 35, 0, 0, 0, 0, 105, 111, 0,
      0, 0, 0, 59, 0, 0, 12, 14, 15, 16,
      0, 0, 0, 46, 45, 0, 0, 0, 52, 103,
      0, 37, 9, 40, 0, 0, 86, 88, 87, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 92, 0, 0, 106, 0,
      19, 21, 23, 31, 0, 0, 30, 0, 44, 11,
      13, 0, 0, 42, 47, 48, 43, 0, 0, 54,
      56, 0, 0, 0, 0, 0, 116, 117, 118, 119,
      120, 121, 0, 82, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 76, 77, 78, 0, 0,
      0, 49, 0, 91, 104, 34, 28, 0, 27, 17,
      58, 57, 53, 55, 38, 109, 0, 112, 0, 0,
      0, 0, 75, 0, 0, 0, 140, 0, 0, 139,
      0, 160, 0, 51, 50, 0, 0, 108, 113, 0,
      0, 142, 0, 0, 143, 141, 0, 0, 137, 0,
      0, 138, 0, 162, 0, 93, 29, 0, 0, 0,
      0, 0, 153, 0, 0, 0, 0, 0, 0, 161,
      0, 149, 150, 152, 151, 144, 145, 148, 147, 146,
      157, 158, 0, 0, 0, 0, 156, 154, 155,
  };
  final static short yydgoto[] = { 15,
      16, 17, 18, 19, 20, 21, 80, 86, 126, 127,
      128, 129, 22, 23, 65, 66, 67, 172, 113, 24,
      69, 25, 72, 176, 26, 114, 141, 142, 81, 133,
      134, 27, 139, 189, 190, 191, 232, 28, 29, 75,
      98, 99, 100, 101, 102, 103, 104, 105, 30, 31,
      32, 33, 34, 62, 195, 237, 196, 35, 36, 197,
      198, 199, 200, 201, 202, 43, 44, 45, 53, 253,
      275, 291,
  };
  final static short yysindex[] = { 113,
      -165, -244, -232, 94, -176, -125, -225, 0, 0, 0,
      0, 101, 261, 0, 0, 113, 0, 0, 0, 0,
      0, 0, -132, -21, -130, 0, 0, 0, -4, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 114, 0, 0, 124, -23, 117, -103, -26,
      -40, 0, -81, -243, 137, 143, -34, -89, 151, 0,
      0, 286, 0, 0, 141, 0, 154, 0, 0, 0,
      -18, 0, 0, 0, -30, 0, 0, -113, -38, 0,
      0, 80, -72, 187, 0, 98, 174, -109, 0, 183,
      194, 0, 0, 0, 0, 153, -30, 186, -73, 123,
      86, 95, 0, 0, 0, 14, 242, 0, 252, 0,
      0, 201, 0, 24, 86, 195, 281, 0, 0, -132,
      -30, -84, 81, 0, 291, -98, 0, 0, 0, 0,
      -132, -130, -106, 0, 0, -130, -9, 97, 0, 0,
      283, 0, 0, 0, -42, 67, 0, 0, 0, 314,
      -36, -30, -30, -30, -30, -30, -30, -30, -30, 67,
      67, 67, 345, 80, -95, 0, 148, 353, 0, 287,
      0, 0, 0, 0, -132, 298, 0, 65, 0, 0,
      0, 3, 0, 0, 0, 0, 0, -21, -104, 0,
      0, -72, 101, 142, -52, 145, 0, 0, 0, 0,
      0, 0, 386, 0, -160, 54, 116, 123, 123, 86,
      86, 86, 86, 95, 95, 0, 0, 0, 85, 394,
      402, 0, 406, 0, 0, 0, 0, 197, 0, 0,
      0, 0, 0, 0, 0, 0, 160, 0, -42, 422,
      -42, 424, 0, 429, -42, 430, 0, -42, 432, 0,
      436, 0, -27, 0, 0, 434, 438, 0, 0, 217,
      218, 0, 220, 225, 0, 0, 12, 226, 0, -138,
      227, 0, -42, 0, -27, 0, 0, 447, 448, 449,
      450, 451, 0, 452, 453, 454, 455, 456, 458, 0,
      462, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 169, -19, 460, 461, 0, 0, 0,
  };
  final static short yyrindex[] = { 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, -57, 0, 0, 0, 506, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 457, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 464, 0, 64, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 468, 75,
      92, 1, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 76, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      387, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 20, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 17, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 105, 112, 129,
      147, 171, 332, 26, 51, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0,
  };
  final static short yygindex[] = { 0,
      0, 495, 0, 0, 0, 27, 41, 0, 0, 390,
      0, 0, -35, -49, 382, 399, 348, 0, -22, -105,
      336, 381, 23, 397, -15, 0, 0, 334, 477, 465,
      -99, 29, 250, 0, 339, 0, 0, 0, 0, 0,
      433, 0, 53, 330, 273, 310, 0, -195, 0, 0,
      0, 0, 0, 0, 209, 0, -101, 0, 0, 39,
      59, 66, 413, 439, 445, 0, 0, 0, 0, 0,
      0, 0,
  };
  final static int YYTABLESIZE = 605;
  static short yytable[];
  static {
    yytable();
  }

  static void yytable() {
    yytable = new short[] { 97,
        71, 14, 135, 60, 96, 97, 112, 14, 70, 97,
        96, 125, 107, 79, 96, 125, 79, 96, 184, 84,
        233, 123, 68, 252, 307, 72, 180, 138, 131, 48,
        55, 187, 188, 185, 131, 49, 108, 185, 37, 61,
        174, 71, 130, 71, 71, 71, 230, 50, 130, 207,
        73, 37, 124, 56, 37, 283, 74, 274, 38, 71,
        71, 120, 71, 25, 168, 39, 72, 167, 72, 72,
        72, 38, 175, 175, 38, 70, 131, 60, 39, 290,
        194, 39, 109, 188, 72, 72, 194, 72, 119, 85,
        130, 73, 238, 73, 73, 73, 88, 247, 173, 78,
        37, 13, 244, 52, 122, 229, 97, 20, 228, 73,
        73, 96, 73, 46, 47, 62, 70, 285, 46, 70,
        38, 177, 20, 71, 286, 71, 143, 39, 158, 96,
        159, 162, 65, 51, 70, 259, 160, 261, 54, 264,
        57, 161, 25, 268, 48, 63, 271, 64, 72, 71,
        72, 65, 64, 65, 183, 83, 14, 76, 186, 250,
        221, 8, 9, 10, 11, 8, 108, 77, 8, 68,
        8, 289, 70, 73, 82, 73, 8, 9, 10, 11,
        110, 108, 156, 106, 157, 14, 111, 69, 68, 46,
        68, 9, 10, 11, 116, 108, 152, 153, 70, 120,
        70, 306, 138, 14, 208, 209, 69, 140, 69, 239,
        240, 66, 14, 58, 121, 90, 60, 144, 4, 205,
        84, 5, 97, 145, 4, 7, 151, 5, 91, 87,
        66, 7, 66, 146, 91, 13, 8, 193, 91, 92,
        93, 94, 95, 193, 166, 92, 93, 94, 95, 92,
        93, 94, 95, 93, 94, 95, 71, 71, 71, 46,
        71, 71, 71, 71, 71, 8, 236, 71, 71, 73,
        71, 71, 71, 71, 282, 71, 71, 71, 71, 163,
        71, 72, 72, 72, 258, 72, 72, 72, 72, 72,
        60, 194, 72, 72, 48, 72, 72, 72, 72, 164,
        72, 72, 72, 72, 14, 72, 73, 73, 73, 165,
        73, 73, 73, 73, 73, 245, 246, 73, 73, 169,
        73, 73, 73, 73, 170, 73, 73, 73, 73, 14,
        73, 70, 70, 70, 179, 70, 70, 70, 70, 70,
        251, 192, 70, 70, 62, 62, 92, 93, 94, 95,
        70, 70, 70, 70, 204, 70, 9, 10, 11, 206,
        108, 65, 65, 65, 65, 93, 94, 95, 1, 2,
        3, 8, 67, 4, 63, 63, 5, 248, 249, 6,
        7, 64, 64, 13, 219, 60, 115, 8, 9, 10,
        11, 67, 12, 67, 154, 155, 224, 58, 68, 68,
        68, 68, 4, 223, 115, 5, 241, 242, 13, 7,
        118, 225, 40, 220, 222, 58, 69, 69, 69, 69,
        4, 193, 227, 5, 304, 40, 243, 7, 40, 4,
        214, 215, 5, 147, 148, 149, 7, 254, 41, 193,
        66, 66, 66, 66, 42, 255, 256, 260, 193, 263,
        115, 41, 257, 267, 41, 203, 270, 42, 132, 136,
        42, 8, 9, 10, 11, 262, 108, 265, 136, 216,
        217, 218, 266, 269, 40, 272, 273, 276, 277, 278,
        279, 288, 280, 210, 211, 212, 213, 281, 284, 287,
        292, 293, 294, 295, 296, 297, 298, 299, 300, 301,
        41, 302, 303, 308, 309, 1, 42, 123, 61, 36,
        63, 305, 182, 136, 97, 181, 58, 136, 171, 178,
        59, 4, 226, 231, 5, 235, 89, 234, 7, 150,
        0, 0, 0, 0, 0, 8, 9, 10, 11, 0,
        12, 58, 0, 137, 0, 117, 4, 0, 0, 5,
        0, 0, 0, 7, 0, 0, 0, 0, 0, 0,
        8, 9, 10, 11, 0, 12, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 67, 67, 67, 67,
    };
  }

  static short yycheck[];
  static {
    yycheck();
  }

  static void yycheck() {
    yycheck = new short[] { 40,
        0, 44, 41, 61, 45, 40, 41, 44, 24, 40,
        45, 125, 256, 40, 45, 125, 40, 45, 125, 123,
        125, 40, 44, 219, 44, 0, 125, 123, 78, 1,
        256, 41, 138, 133, 84, 280, 280, 137, 0, 13,
        125, 41, 78, 43, 44, 45, 44, 280, 84, 151,
        0, 13, 75, 279, 16, 44, 61, 253, 0, 59,
        60, 59, 62, 44, 41, 0, 41, 44, 43, 44,
        45, 13, 122, 123, 16, 0, 126, 61, 13, 275,
        123, 16, 54, 189, 59, 60, 123, 62, 62, 49,
        126, 41, 194, 43, 44, 45, 123, 44, 121, 123,
        62, 123, 263, 280, 123, 41, 40, 44, 44, 59,
        60, 45, 62, 279, 280, 41, 41, 256, 279, 44,
        62, 41, 59, 123, 263, 125, 86, 62, 43, 45,
        45, 37, 41, 40, 59, 237, 42, 239, 264, 241,
        40, 47, 123, 245, 125, 41, 248, 280, 123, 280,
        125, 60, 41, 62, 132, 259, 44, 44, 136, 44,
        256, 275, 276, 277, 278, 275, 280, 44, 275, 41,
        275, 273, 188, 123, 58, 125, 275, 276, 277, 278,
        44, 280, 60, 265, 62, 44, 44, 41, 60, 279,
        62, 276, 277, 278, 44, 280, 270, 271, 123, 59,
        125, 303, 123, 44, 152, 153, 60, 280, 62, 262,
        263, 41, 44, 256, 61, 256, 274, 44, 261, 256,
        123, 264, 280, 41, 261, 268, 41, 264, 269, 256,
        60, 268, 62, 40, 269, 123, 275, 280, 269, 280,
        281, 282, 283, 280, 44, 280, 281, 282, 283, 280,
        281, 282, 283, 281, 282, 283, 256, 257, 258, 279,
        260, 261, 262, 263, 264, 275, 125, 267, 268, 274,
        270, 271, 272, 273, 263, 275, 276, 277, 278, 266,
        280, 256, 257, 258, 125, 260, 261, 262, 263, 264,
        274, 123, 267, 268, 275, 270, 271, 272, 273, 58,
        275, 276, 277, 278, 44, 280, 256, 257, 258, 58,
        260, 261, 262, 263, 264, 262, 263, 267, 268, 125,
        270, 271, 272, 273, 44, 275, 276, 277, 278, 44,
        280, 256, 257, 258, 44, 260, 261, 262, 263, 264,
        256, 59, 267, 268, 270, 271, 280, 281, 282, 283,
        275, 276, 277, 278, 41, 280, 276, 277, 278, 151,
        280, 270, 271, 272, 273, 281, 282, 283, 256, 257,
        258, 275, 41, 261, 270, 271, 264, 262, 263, 267,
        268, 270, 271, 123, 40, 125, 57, 275, 276, 277,
        278, 60, 280, 62, 272, 273, 44, 256, 270, 271,
        272, 273, 261, 256, 75, 264, 262, 263, 123, 268,
        125, 125, 0, 164, 165, 256, 270, 271, 272, 273,
        261, 280, 125, 264, 256, 13, 41, 268, 16, 261,
        158, 159, 264, 281, 282, 283, 268, 44, 0, 280,
        270, 271, 272, 273, 0, 44, 41, 239, 280, 241,
        121, 13, 256, 245, 16, 146, 248, 13, 78, 79,
        16, 275, 276, 277, 278, 44, 280, 44, 88, 160,
        161, 162, 44, 44, 62, 44, 41, 44, 41, 263,
        263, 273, 263, 154, 155, 156, 157, 263, 263, 263,
        44, 44, 44, 44, 44, 44, 44, 44, 44, 44,
        62, 44, 41, 44, 44, 0, 62, 44, 41, 123,
        16, 303, 131, 133, 58, 126, 256, 137, 120, 123,
        260, 261, 175, 188, 264, 192, 50, 189, 268, 97,
        -1, -1, -1, -1, -1, 275, 276, 277, 278, -1,
        280, 256, -1, 79, -1, 260, 261, -1, -1, 264,
        -1, -1, -1, 268, -1, -1, -1, -1, -1, -1,
        275, 276, 277, 278, -1, 280, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, 270, 271, 272, 273,
    };
  }

  final static short YYFINAL = 15;
  final static short YYMAXTOKEN = 283;
  final static String yyname[] = {
      "end-of-file", null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, "'%'", null, null, "'('", "')'", "'*'", "'+'",
      "','", "'-'", null, "'/'", null, null, null, null, null, null, null, null, null, null, "':'",
      "';'", "'<'", "'='", "'>'", null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, "'{'", null, "'}'", null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, "CLASS", "INTERFACE", "IMPLEMENT",
      "RETURN", "IF", "ELSE", "END_IF", "FOR", "IN", "RANGE", "IMPL", "PRINT", "TOD",
      "EQUAL_OPERATOR", "NOT_EQUAL_OPERATOR", "GREATER_THAN_OR_EQUAL_OPERATOR",
      "LESS_THAN_OR_EQUAL_OPERATOR", "MINUS_ASSIGN", "VOID", "LONG", "UINT", "DOUBLE",
      "CADENA", "ID", "CTE_DOUBLE", "CTE_UINT", "CTE_LONG",
  };
  final static String yyrule[] = {
      "$accept : program",
      "program : type_declarations",
      "type_declarations : type_declaration",
      "type_declarations : type_declarations type_declaration",
      "type_declaration : class_declaration",
      "type_declaration : interface_declaration",
      "type_declaration : implement_for_declaration",
      "type_declaration : block_statement",
      "class_declaration : CLASS ID class_body",
      "class_declaration : CLASS ID interfaces class_body",
      "class_declaration : error ID class_body",
      "class_body : '{' class_body_declarations '}'",
      "class_body_declarations : class_body_declaration",
      "class_body_declarations : class_body_declarations class_body_declaration",
      "class_body_declaration : class_member_declaration",
      "class_member_declaration : field_declaration",
      "class_member_declaration : method_declaration",
      "field_declaration : type variable_declarators ','",
      "variable_declarators : variable_declarator",
      "variable_declarators : variable_declarators ';' variable_declarator",
      "variable_declarator : variable_declarator_id",
      "variable_declarator : variable_declarator_id '=' variable_initializer",
      "variable_declarator_id : ID",
      "variable_initializer : arithmetic_operation",
      "method_declaration : method_header method_body",
      "method_header : result_type method_declarator",
      "result_type : VOID",
      "method_declarator : ID '(' formal_parameter ')'",
      "method_declarator : ID '{' formal_parameter '}'",
      "method_declarator : ID '(' formal_parameter ',' error ')'",
      "method_declarator : ID '(' ')'",
      "method_declarator : ID '{' '}'",
      "method_body : block",
      "method_body : ','",
      "formal_parameter : type variable_declarator_id",
      "real_parameter : arithmetic_operation",
      "interfaces : IMPLEMENT interface_type_list",
      "interface_type_list : type_name",
      "interface_type_list : interface_type_list ';' type_name",
      "interface_declaration : INTERFACE ID interface_body",
      "interface_declaration : INTERFACE ID error ','",
      "interface_declaration : error ID interface_body",
      "interface_body : '{' interface_member_declaration '}'",
      "interface_body : '(' interface_member_declaration ')'",
      "interface_body : '{' '}' ','",
      "interface_body : '(' ')'",
      "interface_member_declaration : interface_method_declaration",
      "interface_member_declaration : interface_member_declaration interface_method_declaration",
      "interface_method_declaration : result_type method_declarator",
      "implement_for_declaration : IMPL FOR reference_type ':' implement_for_body",
      "implement_for_declaration : IMPL FOR reference_type ':' error ','",
      "implement_for_declaration : IMPL FOR error ':' implement_for_body ','",
      "implement_for_declaration : error reference_type ':' implement_for_body",
      "implement_for_body : '{' implement_for_body_declarations '}'",
      "implement_for_body_declarations : implement_for_body_declaration",
      "implement_for_body_declarations : implement_for_body_declarations implement_for_body_declaration",
      "implement_for_body_declaration : implement_for_method_declaration",
      "implement_for_method_declaration : method_header implement_for_method_body",
      "implement_for_method_body : method_body",
      "assignment : left_hand_side assignment_operator arithmetic_operation",
      "left_hand_side : ID",
      "expression : equality_expression",
      "equality_expression : relational_expression",
      "equality_expression : equality_expression EQUAL_OPERATOR relational_expression",
      "equality_expression : equality_expression NOT_EQUAL_OPERATOR relational_expression",
      "relational_expression : additive_expression",
      "relational_expression : relational_expression '<' additive_expression",
      "relational_expression : relational_expression '>' additive_expression",
      "relational_expression : relational_expression GREATER_THAN_OR_EQUAL_OPERATOR additive_expression",
      "relational_expression : relational_expression LESS_THAN_OR_EQUAL_OPERATOR additive_expression",
      "arithmetic_operation : additive_expression",
      "additive_expression : multiplicative_expression",
      "additive_expression : additive_expression '+' multiplicative_expression",
      "additive_expression : additive_expression '-' multiplicative_expression",
      "multiplicative_expression : unary_expression",
      "multiplicative_expression : TOD '(' unary_expression ')'",
      "multiplicative_expression : multiplicative_expression '*' unary_expression",
      "multiplicative_expression : multiplicative_expression '/' unary_expression",
      "multiplicative_expression : multiplicative_expression '%' unary_expression",
      "unary_expression : term",
      "unary_expression : ID",
      "term : factor",
      "term : '(' expression ')'",
      "factor : CTE_DOUBLE",
      "factor : CTE_UINT",
      "factor : CTE_LONG",
      "factor : '-' CTE_DOUBLE",
      "factor : '-' CTE_LONG",
      "factor : '-' CTE_UINT",
      "assignment_operator : '='",
      "assignment_operator : MINUS_ASSIGN",
      "method_invocation : ID '(' real_parameter ')' ','",
      "method_invocation : ID '(' ')' ','",
      "method_invocation : ID '(' real_parameter ',' error ')' ','",
      "type : primitive_type",
      "type : reference_type",
      "primitive_type : numeric_type",
      "reference_type : ID",
      "numeric_type : integral_type",
      "numeric_type : floating_type",
      "integral_type : UINT",
      "integral_type : LONG",
      "floating_type : DOUBLE",
      "type_name : ID",
      "block : '{' block_statements RETURN ',' '}'",
      "block : '{' block_statements '}'",
      "block : '{' RETURN ',' '}'",
      "block : '{' '}'",
      "executable_block : '{' executable_block_statements '}'",
      "executable_block : '{' '}'",
      "block_statements : block_statement",
      "block_statements : block_statements block_statement",
      "executable_block_statements : executable_statament",
      "executable_block_statements : executable_block_statements executable_statament",
      "block_statement : local_variable_declaration_statement",
      "block_statement : statement",
      "executable_statament : if_then_statement",
      "executable_statament : if_then_else_statement",
      "executable_statament : for_in_range_statement",
      "executable_statament : print_statement",
      "executable_statament : expression_statement",
      "executable_statament : empty_statement",
      "local_variable_declaration_statement : local_variable_declaration ','",
      "local_variable_declaration : type variable_declarators",
      "statement : statement_without_trailing_substatement",
      "statement : if_then_statement",
      "statement : if_then_else_statement",
      "statement : for_in_range_statement",
      "statement : method_declaration",
      "statement : print_statement",
      "statement_without_trailing_substatement : block",
      "statement_without_trailing_substatement : empty_statement",
      "statement_without_trailing_substatement : expression_statement",
      "expression_statement : statement_expression",
      "statement_expression : assignment",
      "statement_expression : method_invocation",
      "empty_statement : ','",
      "if_then_statement : IF '(' expression ')' executable_block END_IF ','",
      "if_then_statement : IF '(' expression ')' executable_statament END_IF ','",
      "if_then_statement : IF '(' expression ')' executable_statament ','",
      "if_then_statement : IF '(' expression ')' executable_block ','",
      "if_then_statement : IF '(' expression ')' error END_IF ','",
      "if_then_statement : IF '(' error ')' executable_block END_IF ','",
      "if_then_statement : IF '(' error ')' executable_statament END_IF ','",
      "if_then_else_statement : IF '(' expression ')' executable_block ELSE executable_block END_IF ','",
      "if_then_else_statement : IF '(' expression ')' executable_block ELSE executable_statament END_IF ','",
      "if_then_else_statement : IF '(' expression ')' executable_statament ELSE executable_statament END_IF ','",
      "if_then_else_statement : IF '(' expression ')' executable_statament ELSE executable_block END_IF ','",
      "if_then_else_statement : IF '(' expression ')' executable_statament ELSE executable_block error ','",
      "if_then_else_statement : IF '(' error ')' executable_block ELSE executable_block END_IF ','",
      "if_then_else_statement : IF '(' error ')' executable_block ELSE executable_statament END_IF ','",
      "if_then_else_statement : IF '(' error ')' executable_statament ELSE executable_statament END_IF ','",
      "if_then_else_statement : IF '(' error ')' executable_statament ELSE executable_block END_IF ','",
      "if_then_else_statement : IF '(' expression ')' executable_block ELSE executable_block ','",
      "for_in_range_statement : FOR for_variable IN RANGE '(' for_init for_end for_update ')' executable_block ','",
      "for_in_range_statement : FOR for_variable IN RANGE '(' for_init for_end for_update ')' executable_statament ','",
      "for_in_range_statement : FOR for_variable IN RANGE '(' for_init for_end for_update ')' error ','",
      "for_in_range_statement : FOR for_variable IN RANGE '(' error ')' executable_block ','",
      "for_in_range_statement : FOR for_variable IN RANGE '(' error ')' executable_statament ','",
      "for_variable : ID",
      "for_init : factor",
      "for_update : factor",
      "for_end : factor",
      "print_statement : PRINT CADENA ','",
      "print_statement : PRINT error ','",
      "print_statement : error CADENA ','",
  };

  // #line 372 "grammer.y"

  private static AnalizadorLexico aLexico;
  private static int yylval_recognition = 0;
  public static boolean error = false;

  // This method is the one where BYACC/J expects to obtain its input tokens.
  // Wrap any file/string scanning code you have in this function. This method
  // should return <0 if there is an error, and 0 when it encounters the end of
  // input. See the examples to clarify what we mean.
  int yylex() {
    Tupla<String, Short> t = aLexico.generateToken();
    String lexema = t.getFirst();
    Short token = t.getSecond();

    if (lexema != null) {
      yylval = new ParserVal(lexema);
      yylval_recognition += 1;
    }

    return token;
  }

  // This method is expected by BYACC/J, and is used to provide error messages to
  // be directed to the channels the user desires.
  void yyerror(String msg) {
    System.out.println("Error en el parser: " + msg);
  }

  // ###############################################################
  // metodos auxiliares a la gramatica
  // ###############################################################

  private String negarDouble(String lexema) {

    double RDN_MIN = -2.2250738585072014D * -Math.pow(10, 308);
    double RDN_MAX = -1.7976931348623157D * Math.pow(10, 308);

    String n_lexema = '-' + lexema;
    double numero = 0.0;

    try {
      numero = Double.parseDouble(n_lexema);
    } catch (Exception ex) {
    }

    if (numero > RDN_MAX || numero < RDN_MIN) {
      Logger.logWarning(aLexico.getProgramPosition(),
          "El DOUBLE se excedio de rango, el mismo fue truncado al valor " + RDN_MAX + ".");
      n_lexema = "" + RDN_MAX;
    }

    addTablaSimbolos(lexema, n_lexema, "D");

    return n_lexema;
  }

  private void addTablaSimbolos(String lexema, String n_lexema, String tipo) {

    if (!TablaSimbolos.containsKey(n_lexema)) {

      if (tipo == "D") { // Perdon Luis por hacer un if por tipos T_T
        TablaSimbolos.addDouble(n_lexema);

      } else {
        TablaSimbolos.addLong(n_lexema);
      }

      TablaSimbolos.addContador(n_lexema);
      TablaSimbolos.decreaseCounter(lexema);

    } else {
      TablaSimbolos.increaseCounter(n_lexema);
    }
  }

  private String negarLong(String lexema) {

    String n_lexema = '-' + lexema;
    long numero = 0;

    try {
      numero = Long.parseLong(n_lexema);
    } catch (Exception ex) {
    }

    addTablaSimbolos(lexema, n_lexema, "L");

    return n_lexema;
  }

  // ###############################################################
  // metodos de lectura de los programadas
  // ###############################################################

  private static ArrayList<String> listFilesInDirectory(String path) {
    // Obtén el directorio actual
    File element = new File(System.getProperty("user.dir") + "/" + path);
    ArrayList<String> out = new ArrayList<>();

    // Verifica si es un directorio o archivo válido
    if (element.isDirectory() || element.isFile()) {
      // Lista de archivos y directorios en el directorio actual
      File[] filesAndDirs = element.listFiles();

      // Itera a través de los archivos y directorios
      int i = 0;
      for (File fileOrDir : filesAndDirs) {
        String name = fileOrDir.getName();
        System.out.println("[" + i + "]" + ": " + name);
        out.add(name);
        i++;
      }
    } else {
      System.err.println("No es un directorio válido.");
    }

    return out;
  }

  private static String generatePath() {
    ArrayList<String> directories = listFilesInDirectory("sample_programs");
    String path = "";

    if (!directories.isEmpty()) {
      Scanner scanner = new Scanner(System.in);
      int indice = -1;

      while (indice < 0) {
        System.out.print("Ingrese el numero de carpeta a acceder: ");
        String input = scanner.nextLine();
        indice = Integer.parseInt(input);
        if (indice < directories.size() || indice >= 0) {
          path = directories.get(indice);
          directories = listFilesInDirectory("sample_programs" + "/" + path);
        } else {
          System.out.println("El indice no es correcto, ingrese nuevamente...");
          indice = -1;
        }

      }

      if (!directories.isEmpty()) {
        indice = -1;

        while (indice < 0) {

          System.out.print("Ingrese el numero de archivo binario a compilar: ");
          String input = scanner.nextLine();
          indice = Integer.parseInt(input);

          if (indice < directories.size() || indice >= 0) {
            path += "/" + directories.get(Integer.parseInt(input));
          } else {

            System.out.println("El indice no es correcto, ingrese nuevamente...");
            indice = -1;
          }

        }
      }
      scanner.close();
    }
    return path;
  }

  public static void main(String[] args) throws IOException {
    System.out.println("Iniciando compilacion... ");

    String input = generatePath();

    aLexico = new AnalizadorLexico(input);

    if (!aLexico.hasReadWell()) {
      return;
    }

    Parser aSintactico = new Parser();
    aSintactico.run();
    aSintactico.dump_stacks(yylval_recognition);

    System.out.println(Logger.dumpLog());
    System.out.println(aLexico.getProgram());
  }

  // #line 793 "Parser.java"
  // ###############################################################
  // method: yylexdebug : check lexer state
  // ###############################################################
  void yylexdebug(int state, int ch) {
    String s = null;
    if (ch < 0)
      ch = 0;
    if (ch <= YYMAXTOKEN) // check index bounds
      s = yyname[ch]; // now get it
    if (s == null)
      s = "illegal-symbol";
    debug("state " + state + ", reading " + ch + " (" + s + ")");
  }

  // The following are now global, to aid in error reporting
  int yyn; // next next thing to do
  int yym; //
  int yystate; // current parsing state from state table
  String yys; // current token string

  // ###############################################################
  // method: yyparse : parse input and execute indicated items
  // ###############################################################
  int yyparse() {
    boolean doaction;
    init_stacks();
    yynerrs = 0;
    yyerrflag = 0;
    yychar = -1; // impossible char forces a read
    yystate = 0; // initial state
    state_push(yystate); // save it
    val_push(yylval); // save empty value
    while (true) // until parsing is done, either correctly, or w/error
    {
      doaction = true;
      if (yydebug)
        debug("loop");
      // #### NEXT ACTION (from reduction table)
      for (yyn = yydefred[yystate]; yyn == 0; yyn = yydefred[yystate]) {
        if (yydebug)
          debug("yyn:" + yyn + "  state:" + yystate + "  yychar:" + yychar);
        if (yychar < 0) // we want a char?
        {
          yychar = yylex(); // get next token
          if (yydebug)
            debug(" next yychar:" + yychar);
          // #### ERROR CHECK ####
          if (yychar < 0) // it it didn't work/error
          {
            yychar = 0; // change it to default string (no -1!)
            if (yydebug)
              yylexdebug(yystate, yychar);
          }
        } // yychar<0
        yyn = yysindex[yystate]; // get amount to shift by (shift index)
        if ((yyn != 0) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar) {
          if (yydebug)
            debug("state " + yystate + ", shifting to state " + yytable[yyn]);
          // #### NEXT STATE ####
          yystate = yytable[yyn];// we are in a new state
          state_push(yystate); // save it
          val_push(yylval); // push our lval as the input for next rule
          yychar = -1; // since we have 'eaten' a token, say we need another
          if (yyerrflag > 0) // have we recovered an error?
            --yyerrflag; // give ourselves credit
          doaction = false; // but don't process yet
          break; // quit the yyn=0 loop
        }

        yyn = yyrindex[yystate]; // reduce
        if ((yyn != 0) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar) { // we reduced!
          if (yydebug)
            debug("reduce");
          yyn = yytable[yyn];
          doaction = true; // get ready to execute
          break; // drop down to actions
        } else // ERROR RECOVERY
        {
          if (yyerrflag == 0) {
            yyerror("syntax error");
            yynerrs++;
          }
          if (yyerrflag < 3) // low error count?
          {
            yyerrflag = 3;
            while (true) // do until break
            {
              if (stateptr < 0) // check for under & overflow here
              {
                yyerror("stack underflow. aborting..."); // note lower case 's'
                return 1;
              }
              yyn = yysindex[state_peek(0)];
              if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                  yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE) {
                if (yydebug)
                  debug("state " + state_peek(0) + ", error recovery shifting to state " + yytable[yyn] + " ");
                yystate = yytable[yyn];
                state_push(yystate);
                val_push(yylval);
                doaction = false;
                break;
              } else {
                if (yydebug)
                  debug("error recovery discarding state " + state_peek(0) + " ");
                if (stateptr < 0) // check for under & overflow here
                {
                  yyerror("Stack underflow. aborting..."); // capital 'S'
                  return 1;
                }
                state_pop();
                val_pop();
              }
            }
          } else // discard this token
          {
            if (yychar == 0)
              return 1; // yyabort
            if (yydebug) {
              yys = null;
              if (yychar <= YYMAXTOKEN)
                yys = yyname[yychar];
              if (yys == null)
                yys = "illegal-symbol";
              debug("state " + yystate + ", error recovery discards token " + yychar + " (" + yys + ")");
            }
            yychar = -1; // read another
          }
        } // end error recovery
      } // yyn=0 loop
      if (!doaction) // any reason not to proceed?
        continue; // skip action
      yym = yylen[yyn]; // get count of terminals on rhs
      if (yydebug)
        debug("state " + yystate + ", reducing " + yym + " by rule " + yyn + " (" + yyrule[yyn] + ")");
      if (yym > 0) // if count of rhs not 'nil'
        yyval = val_peek(yym - 1); // get current semantic value
      yyval = dup_yyval(yyval); // duplicate yyval if ParserVal is used as semantic value
      switch (yyn) {
        // ########## USER-SUPPLIED ACTIONS ##########
        case 1:
        // #line 31 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio el programa.");
        }
          break;
        case 8:
        // #line 49 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una CLASS.");
        }
          break;
        case 9:
        // #line 50 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una CLASS que implementa una interface.");
        }
          break;
        case 10:
        // #line 51 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Declaracion de CLASS no valida.");
        }
          break;
        case 17:
        // #line 68 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una declaracion de atributo/s.");
        }
          break;
        case 27:
        // #line 94 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un metodo.");
        }
          break;
        case 28:
        // #line 95 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(),
              "La declaracion de un metodo debe estar limitado por parentesis \"(...)\".");
        }
          break;
        case 29:
        // #line 96 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Solo se permite la declaracion de un unico parametro formal.");
        }
          break;
        case 30:
        // #line 97 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un metodo.");
        }
          break;
        case 31:
        // #line 98 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(),
              "La declaracion de un metodo debe estar limitado por parentesis \"(...)\".");
        }
          break;
        case 39:
        // #line 120 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una INTERFACE.");
        }
          break;
        case 40:
        // #line 121 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Es necesario definir el cuerpo de la interface.");
        }
          break;
        case 41:
        // #line 122 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Declaracion de INTERFACE no valida.");
        }
          break;
        case 43:
        // #line 126 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(),
              "El cuerpo de la interface debe estar limitado por llaves \"{...}\".");
        }
          break;
        case 45:
        // #line 128 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(),
              "El cuerpo de la interface debe estar limitado por llaves \"{...}\".");
        }
          break;
        case 49:
        // #line 138 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un IMPL FOR.");
        }
          break;
        case 50:
        // #line 139 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Es necesario implementar el cuerpo del metodo.");
        }
          break;
        case 51:
        // #line 140 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Se debe referenciar a una clase.");
        }
          break;
        case 52:
        // #line 141 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Declaracion de IMPL FOR no valida.");
        }
          break;
        case 59:
        // #line 165 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una asignacion.");
        }
          break;
        case 61:
        // #line 171 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica.");
        }
          break;
        case 70:
        // #line 186 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion aritmetica.");
        }
          break;
        case 86:
        // #line 212 "grammer.y"
        {
          System.out.println("Posicion 1: " + val_peek(1).sval + ", Posicion 2: " + val_peek(0).sval);
          yyval = new ParserVal(negarDouble(val_peek(0).sval));
        }
          break;
        case 87:
        // #line 213 "grammer.y"
        {
          System.out.println(val_peek(0).sval);
          yyval = new ParserVal(negarLong(val_peek(0).sval));
        }
          break;
        case 88:
        // #line 214 "grammer.y"
        {
          Logger.logWarning(aLexico.getProgramPosition(), "Los tipos enteros deben ser sin signo.");
          yyval = new ParserVal(val_peek(0).sval);
        }
          break;
        case 91:
        // #line 221 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo.");
        }
          break;
        case 92:
        // #line 222 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo.");
        }
          break;
        case 93:
        // #line 223 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Solo se permite el pasaje de un parametro real.");
        }
          break;
        case 105:
        // #line 266 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");
        }
          break;
        case 107:
        // #line 268 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");
        }
          break;
        case 137:
        // #line 326 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF.");
        }
          break;
        case 138:
        // #line 327 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF.");
        }
          break;
        case 139:
        // #line 328 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(),
              "Es necesario declarar el final de la sentencia de control IF.");
        }
          break;
        case 140:
        // #line 329 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(),
              "Es necesario declarar el final de la sentencia de control IF.");
        }
          break;
        case 141:
        // #line 330 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(),
              "Es necesario declarar el cuerpo de la sentencia de control IF.");
        }
          break;
        case 142:
        // #line 331 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");
        }
          break;
        case 143:
        // #line 332 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");
        }
          break;
        case 144:
        // #line 335 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");
        }
          break;
        case 145:
        // #line 336 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");
        }
          break;
        case 146:
        // #line 337 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");
        }
          break;
        case 147:
        // #line 338 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");
        }
          break;
        case 148:
        // #line 339 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(),
              "Es necesario declarar el END_IF de la sentencia de control IF.");
        }
          break;
        case 149:
        // #line 340 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");
        }
          break;
        case 150:
        // #line 341 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");
        }
          break;
        case 151:
        // #line 342 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");
        }
          break;
        case 152:
        // #line 343 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");
        }
          break;
        case 154:
        // #line 348 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia FOR IN RANGE.");
        }
          break;
        case 155:
        // #line 349 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia FOR IN RANGE.");
        }
          break;
        case 156:
        // #line 350 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Cuerpo del FOR IN RANGE no valido.");
        }
          break;
        case 157:
        // #line 351 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Condicion del FOR IN RANGE no valido.");
        }
          break;
        case 158:
        // #line 352 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Condicion del FOR IN RANGE no valido.");
        }
          break;
        case 163:
        // #line 367 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition() - 1, "Se reconocio una sentencia PRINT.");
        }
          break;
        case 164:
        // #line 368 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition() - 1, "Se esperaba una cadena.");
        }
          break;
        case 165:
        // #line 369 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition() - 1, "Declaracion de PRINT no valida.");
        }
          break;
        // #line 1158 "Parser.java"
        // ########## END OF USER-SUPPLIED ACTIONS ##########
      }// switch
       // #### Now let's reduce... ####
      if (yydebug)
        debug("reduce");
      state_drop(yym); // we just reduced yylen states
      yystate = state_peek(0); // get new state
      val_drop(yym); // corresponding value drop
      yym = yylhs[yyn]; // select next TERMINAL(on lhs)
      if (yystate == 0 && yym == 0)// done? 'rest' state and at first TERMINAL
      {
        if (yydebug)
          debug("After reduction, shifting from state 0 to state " + YYFINAL + "");
        yystate = YYFINAL; // explicitly say we're done
        state_push(YYFINAL); // and save it
        val_push(yyval); // also save the semantic value of parsing
        if (yychar < 0) // we want another character?
        {
          yychar = yylex(); // get next character
          if (yychar < 0)
            yychar = 0; // clean, if necessary
          if (yydebug)
            yylexdebug(yystate, yychar);
        }
        if (yychar == 0) // Good exit (if lex returns 0 ;-)
          break; // quit the loop--all DONE
      } // if yystate
      else // else not done yet
      { // get next state and push, for next yydefred[]
        yyn = yygindex[yym]; // find out where to go
        if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
          yystate = yytable[yyn]; // get new state
        else
          yystate = yydgoto[yym]; // else go to new defred
        if (yydebug)
          debug("after reduction, shifting from state " + state_peek(0) + " to state " + yystate + "");
        state_push(yystate); // going again, so push state & val...
        val_push(yyval); // for next action
      }
    } // main loop
    return 0;// yyaccept!!
  }
  // ## end of method parse() ######################################

  // ## run() --- for Thread #######################################
  /**
   * A default run method, used for operating this parser
   * object in the background. It is intended for extending Thread
   * or implementing Runnable. Turn off with -Jnorun .
   */
  public void run() {
    yyparse();
  }
  // ## end of method run() ########################################

  // ## Constructors ###############################################
  /**
   * Default constructor. Turn off with -Jnoconstruct .
   * 
   */
  public Parser() {
    // nothing to do
  }

  /**
   * Create a parser, setting the debug to true or false.
   * 
   * @param debugMe true for debugging, false for no debug.
   */
  public Parser(boolean debugMe) {
    yydebug = debugMe;
  }
  // ###############################################################

}
// ################### END OF CLASS ##############################
