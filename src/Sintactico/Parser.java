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
//#line 25 "Parser.java"

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
      0, 1, 1, 2, 2, 2, 2, 3, 3, 7,
      9, 9, 10, 11, 11, 12, 12, 15, 15, 16,
      16, 17, 18, 13, 20, 22, 23, 23, 21, 21,
      24, 26, 8, 27, 27, 4, 29, 29, 30, 30,
      31, 5, 5, 33, 34, 34, 35, 36, 37, 38,
      39, 41, 42, 42, 42, 43, 43, 43, 43, 43,
      19, 44, 44, 44, 45, 45, 45, 45, 45, 46,
      46, 47, 47, 48, 48, 48, 48, 48, 48, 40,
      40, 49, 49, 14, 14, 50, 32, 51, 51, 52,
      52, 53, 28, 25, 25, 25, 25, 55, 55, 54,
      54, 56, 56, 57, 57, 58, 58, 58, 58, 58,
      58, 59, 66, 6, 6, 6, 6, 6, 6, 67,
      67, 67, 64, 68, 68, 65, 60, 60, 60, 60,
      60, 61, 61, 61, 61, 61, 62, 62, 69, 70,
      72, 71, 63, 63,
  };
  final static short yylen[] = { 2,
      1, 1, 2, 1, 1, 1, 1, 3, 4, 3,
      1, 2, 1, 1, 1, 3, 2, 1, 3, 1,
      3, 1, 1, 2, 2, 1, 4, 3, 1, 1,
      2, 1, 2, 1, 3, 3, 3, 3, 1, 2,
      2, 5, 6, 3, 1, 2, 1, 2, 1, 3,
      1, 1, 1, 3, 3, 1, 3, 3, 3, 3,
      1, 1, 3, 3, 1, 4, 3, 3, 3, 1,
      1, 1, 3, 1, 1, 1, 2, 2, 2, 1,
      1, 5, 4, 1, 1, 1, 1, 1, 1, 1,
      1, 1, 1, 5, 3, 4, 2, 3, 2, 1,
      2, 1, 2, 1, 1, 1, 1, 1, 1, 1,
      1, 2, 2, 1, 1, 1, 1, 1, 1, 1,
      1, 1, 1, 1, 1, 1, 7, 7, 7, 6,
      4, 9, 9, 9, 9, 8, 11, 11, 1, 1,
      1, 1, 3, 1,
  };
  final static short yydefred[] = { 0,
      0, 0, 0, 0, 0, 0, 26, 0, 0, 126,
      0, 0, 2, 4, 5, 6, 7, 118, 0, 0,
      120, 124, 0, 125, 115, 116, 117, 119, 122, 121,
      114, 123, 0, 0, 0, 139, 0, 0, 0, 0,
      0, 91, 90, 92, 0, 97, 105, 0, 85, 84,
      86, 88, 89, 0, 100, 104, 0, 3, 30, 24,
      29, 0, 25, 81, 80, 0, 0, 0, 8, 0,
      0, 36, 0, 0, 71, 74, 75, 76, 0, 0,
      0, 0, 0, 0, 0, 65, 70, 72, 0, 87,
      0, 143, 0, 32, 0, 0, 0, 22, 0, 18,
      0, 0, 95, 101, 112, 0, 50, 93, 0, 34,
      0, 0, 11, 13, 14, 15, 0, 9, 0, 0,
      0, 39, 131, 0, 77, 79, 78, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 83, 0, 96, 0, 0, 0, 28, 0,
      0, 0, 17, 10, 12, 0, 38, 41, 37, 40,
      0, 73, 0, 0, 0, 0, 106, 107, 108, 109,
      110, 111, 0, 0, 0, 0, 0, 0, 0, 0,
      67, 68, 69, 0, 0, 0, 42, 82, 19, 21,
      23, 94, 31, 27, 35, 16, 66, 130, 99, 0,
      102, 0, 0, 0, 0, 0, 140, 0, 43, 0,
      0, 45, 47, 98, 103, 0, 0, 127, 129, 0,
      0, 128, 142, 0, 49, 48, 44, 46, 0, 0,
      0, 136, 0, 141, 0, 132, 133, 135, 134, 0,
      0, 0, 137, 138,
  };
  final static short yydgoto[] = { 11,
      12, 13, 14, 15, 16, 17, 69, 70, 112, 113,
      114, 115, 18, 48, 99, 100, 101, 190, 94, 19,
      60, 20, 63, 151, 21, 95, 109, 110, 72, 121,
      122, 49, 187, 211, 212, 213, 226, 22, 23, 66,
      81, 82, 83, 84, 85, 86, 87, 88, 24, 50,
      51, 52, 53, 54, 165, 200, 55, 166, 56, 167,
      168, 169, 170, 171, 172, 57, 31, 32, 37, 208,
      224, 235,
  };
  final static short yysindex[] = { -36,
      -239, -237, 15, -214, -196, -206, 0, 49, 639, 0,
      0, -36, 0, 0, 0, 0, 0, 0, -25, -178,
      0, 0, -59, 0, 0, 0, 0, 0, 0, 0,
      0, 0, -107, -2, -40, 0, -142, -140, 93, -34,
      106, 0, 0, 0, 49, 0, 0, -111, 0, 0,
      0, 0, 0, 663, 0, 0, 120, 0, 0, 0,
      0, 85, 0, 0, 0, 132, -95, -172, 0, 44,
      -116, 0, 126, 133, 0, 0, 0, 0, -91, 132,
      145, -155, -35, -12, 97, 0, 0, 0, -73, 0,
      137, 0, 152, 0, 156, -12, 73, 0, 144, 0,
      141, 160, 0, 0, 0, 142, 0, 0, 147, 0,
      161, -96, 0, 0, 0, 0, -111, 0, 163, -178,
      -113, 0, 0, -27, 0, 0, 0, 168, 207, 132,
      132, 132, 132, 132, 132, 132, 132, -27, -27, -27,
      170, -103, 0, 164, 0, -111, 132, 87, 0, -111,
      172, -95, 0, 0, 0, 55, 0, 0, 0, 0,
      173, 0, 174, 167, -144, -115, 0, 0, 0, 0,
      0, 0, -35, -35, -12, -12, -12, -12, 97, 97,
      0, 0, 0, 143, 175, -48, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 192,
      0, 311, 190, 201, 311, 216, 0, 143, 0, -25,
      -110, 0, 0, 0, 0, 3, 4, 0, 0, -30,
      7, 0, 0, 143, 0, 0, 0, 0, 231, 236,
      241, 0, 247, 0, 254, 0, 0, 0, 0, 311,
      256, 261, 0, 0,
  };
  final static short yyrindex[] = { 0,
      0, 0, 0, 0, 0, 566, 0, -44, 0, 0,
      0, 310, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, -57, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 493, 417, 76, 1, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 6, 0, 0, 272, 0,
      63, 0, 0, 0, 0, 0, 0, 0, 197, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 442, 467, 101, 127, 367, 392, 26, 51,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0,
  };
  final static short yygindex[] = { 0,
      0, 313, 0, 0, 0, 21, 265, 0, 0, 229,
      0, 0, -29, -58, 225, 199, 200, 0, 213, -162,
      165, -49, 246, 0, -16, 0, 0, 218, 0, 0,
      259, 344, 0, 0, 182, 0, 0, 0, 0, 0,
      -28, 0, 2, -4, 20, -60, 0, -150, 0, 0,
      0, 0, 0, 0, -149, 0, 332, -143, 0, 23,
      28, 81, 100, 146, 166, 0, 0, 0, 0, 0,
      0, 0,
  };
  final static int YYTABLESIZE = 943;
  static short yytable[];
  static {
    yytable();
  }

  static void yytable() {
    yytable = new short[] { 80,
        62, 65, 61, 51, 79, 80, 93, 10, 119, 117,
        79, 159, 80, 232, 227, 68, 51, 79, 59, 186,
        201, 120, 25, 210, 134, 63, 135, 26, 154, 47,
        136, 25, 137, 207, 25, 96, 26, 107, 116, 26,
        33, 62, 34, 62, 62, 62, 61, 150, 210, 61,
        64, 128, 216, 117, 35, 220, 215, 223, 217, 62,
        62, 221, 62, 161, 61, 36, 63, 38, 63, 63,
        63, 120, 39, 234, 47, 56, 25, 181, 182, 183,
        27, 26, 116, 111, 63, 63, 9, 63, 40, 27,
        241, 64, 27, 64, 64, 64, 242, 9, 196, 28,
        59, 62, 7, 42, 43, 44, 20, 90, 28, 64,
        64, 28, 64, 146, 130, 131, 56, 202, 203, 56,
        71, 20, 89, 62, 106, 62, 60, 175, 176, 177,
        178, 173, 174, 140, 27, 56, 92, 56, 138, 90,
        204, 59, 96, 139, 59, 29, 205, 206, 63, 97,
        63, 67, 185, 28, 29, 179, 180, 29, 7, 111,
        59, 7, 59, 105, 7, 30, 68, 60, 98, 123,
        60, 80, 124, 64, 30, 64, 79, 30, 7, 42,
        43, 44, 149, 90, 108, 129, 60, 79, 60, 125,
        126, 127, 141, 61, 142, 143, 144, 145, 56, 29,
        56, 147, 146, 148, 153, 152, 157, 188, 162, 184,
        10, 192, 194, 197, 64, 73, 51, 198, 209, 30,
        1, 2, 87, 59, 3, 59, 7, 4, 74, 51,
        5, 6, 231, 218, 74, 10, 132, 133, 7, 75,
        76, 77, 78, 8, 219, 75, 76, 77, 78, 60,
        10, 60, 75, 76, 77, 78, 62, 62, 62, 222,
        62, 62, 62, 62, 62, 229, 230, 62, 62, 233,
        62, 62, 62, 62, 236, 62, 62, 62, 62, 237,
        62, 63, 63, 63, 238, 63, 63, 63, 63, 63,
        239, 199, 63, 63, 240, 63, 63, 63, 63, 243,
        63, 63, 63, 63, 244, 63, 64, 64, 64, 1,
        64, 64, 64, 64, 64, 113, 214, 64, 64, 33,
        64, 64, 64, 64, 58, 64, 64, 64, 64, 164,
        64, 56, 56, 56, 118, 56, 56, 56, 56, 56,
        155, 156, 56, 56, 189, 56, 56, 56, 56, 193,
        56, 56, 56, 56, 10, 56, 59, 59, 59, 191,
        59, 59, 59, 59, 59, 158, 57, 59, 59, 195,
        59, 59, 59, 59, 225, 59, 59, 59, 59, 160,
        59, 91, 60, 60, 60, 104, 60, 60, 60, 60,
        60, 58, 228, 60, 60, 0, 60, 60, 60, 60,
        74, 60, 60, 60, 60, 0, 60, 57, 0, 0,
        57, 75, 76, 77, 78, 0, 53, 42, 43, 44,
        0, 90, 0, 76, 77, 78, 57, 3, 57, 0,
        4, 0, 58, 164, 6, 58, 0, 0, 0, 0,
        0, 54, 0, 0, 0, 0, 8, 0, 0, 0,
        0, 58, 3, 58, 0, 4, 0, 53, 0, 6,
        53, 0, 163, 0, 0, 0, 55, 3, 0, 0,
        4, 8, 0, 0, 6, 0, 0, 0, 0, 0,
        0, 0, 54, 0, 0, 54, 8, 0, 0, 57,
        0, 57, 52, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 55, 0, 0,
        55, 0, 0, 0, 58, 0, 58, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 52, 0, 0, 52, 0, 0, 53,
        0, 53, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 54, 144, 54, 0, 0, 0,
        0, 3, 0, 0, 4, 0, 0, 0, 6, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 55,
        8, 55, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 144,
        0, 0, 0, 0, 0, 52, 0, 52, 0, 0,
        0, 0, 57, 57, 57, 0, 57, 57, 57, 57,
        57, 0, 0, 57, 57, 0, 57, 57, 57, 57,
        0, 57, 57, 57, 57, 0, 57, 58, 58, 58,
        0, 58, 58, 58, 58, 58, 0, 0, 58, 58,
        0, 58, 58, 58, 58, 0, 58, 58, 58, 58,
        0, 58, 53, 53, 53, 0, 53, 53, 53, 53,
        53, 0, 10, 53, 53, 0, 53, 53, 144, 0,
        144, 53, 53, 53, 53, 0, 53, 54, 54, 54,
        0, 54, 54, 54, 54, 54, 10, 0, 54, 54,
        0, 54, 54, 0, 0, 0, 54, 54, 54, 54,
        0, 54, 55, 55, 55, 0, 55, 55, 55, 55,
        55, 0, 0, 55, 55, 0, 55, 55, 0, 0,
        0, 55, 55, 55, 55, 0, 55, 0, 52, 52,
        52, 0, 52, 52, 52, 52, 52, 0, 0, 52,
        52, 9, 0, 46, 0, 0, 0, 52, 52, 52,
        52, 0, 52, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 9, 0, 103, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 144, 144, 144, 0, 144, 144, 144, 144, 144,
        0, 0, 144, 144, 0, 0, 0, 0, 0, 0,
        144, 144, 144, 144, 0, 144, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 41, 3,
        0, 0, 4, 0, 0, 0, 6, 0, 0, 0,
        0, 0, 0, 7, 42, 43, 44, 0, 45, 0,
        0, 0, 102, 3, 0, 0, 4, 0, 0, 0,
        6, 0, 0, 0, 0, 0, 0, 7, 42, 43,
        44, 0, 45,
    };
  }

  static short yycheck[];
  static {
    yycheck();
  }

  static void yycheck() {
    yycheck = new short[] { 40,
        0, 61, 19, 61, 45, 40, 41, 44, 125, 68,
        45, 125, 40, 44, 125, 123, 61, 45, 44, 123,
        164, 71, 0, 186, 60, 0, 62, 0, 125, 9,
        43, 9, 45, 184, 12, 40, 9, 66, 68, 12,
        280, 41, 280, 43, 44, 45, 41, 106, 211, 44,
        0, 80, 202, 112, 40, 205, 200, 208, 202, 59,
        60, 205, 62, 124, 59, 280, 41, 264, 43, 44,
        45, 121, 279, 224, 54, 0, 54, 138, 139, 140,
        0, 54, 112, 256, 59, 60, 123, 62, 40, 9,
        240, 41, 12, 43, 44, 45, 240, 123, 44, 0,
        0, 280, 275, 276, 277, 278, 44, 280, 9, 59,
        60, 12, 62, 59, 270, 271, 41, 262, 263, 44,
        123, 59, 265, 123, 40, 125, 0, 132, 133, 134,
        135, 130, 131, 37, 54, 60, 44, 62, 42, 280,
        256, 41, 147, 47, 44, 0, 262, 263, 123, 44,
        125, 259, 256, 54, 9, 136, 137, 12, 275, 256,
        60, 275, 62, 44, 275, 0, 123, 41, 280, 44,
        44, 40, 40, 123, 9, 125, 45, 12, 275, 276,
        277, 278, 41, 280, 280, 41, 60, 45, 62, 281,
        282, 283, 266, 210, 58, 44, 41, 125, 123, 54,
        125, 61, 59, 44, 44, 59, 44, 44, 41, 40,
        44, 125, 41, 41, 274, 256, 274, 44, 44, 54,
        257, 258, 280, 123, 261, 125, 275, 264, 269, 274,
        267, 268, 263, 44, 269, 44, 272, 273, 275, 280,
        281, 282, 283, 280, 44, 280, 281, 282, 283, 123,
        44, 125, 280, 281, 282, 283, 256, 257, 258, 44,
        260, 261, 262, 263, 264, 263, 263, 267, 268, 263,
        270, 271, 272, 273, 44, 275, 276, 277, 278, 44,
        280, 256, 257, 258, 44, 260, 261, 262, 263, 264,
        44, 125, 267, 268, 41, 270, 271, 272, 273, 44,
        275, 276, 277, 278, 44, 280, 256, 257, 258, 0,
        260, 261, 262, 263, 264, 44, 125, 267, 268, 123,
        270, 271, 272, 273, 12, 275, 276, 277, 278, 123,
        280, 256, 257, 258, 70, 260, 261, 262, 263, 264,
        112, 117, 267, 268, 146, 270, 271, 272, 273, 150,
        275, 276, 277, 278, 44, 280, 256, 257, 258, 147,
        260, 261, 262, 263, 264, 120, 0, 267, 268, 152,
        270, 271, 272, 273, 210, 275, 276, 277, 278, 121,
        280, 38, 256, 257, 258, 54, 260, 261, 262, 263,
        264, 0, 211, 267, 268, -1, 270, 271, 272, 273,
        269, 275, 276, 277, 278, -1, 280, 41, -1, -1,
        44, 280, 281, 282, 283, -1, 0, 276, 277, 278,
        -1, 280, -1, 281, 282, 283, 60, 261, 62, -1,
        264, -1, 41, 123, 268, 44, -1, -1, -1, -1,
        -1, 0, -1, -1, -1, -1, 280, -1, -1, -1,
        -1, 60, 261, 62, -1, 264, -1, 41, -1, 268,
        44, -1, 256, -1, -1, -1, 0, 261, -1, -1,
        264, 280, -1, -1, 268, -1, -1, -1, -1, -1,
        -1, -1, 41, -1, -1, 44, 280, -1, -1, 123,
        -1, 125, 0, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, 41, -1, -1,
        44, -1, -1, -1, 123, -1, 125, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, 41, -1, -1, 44, -1, -1, 123,
        -1, 125, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, 123, 0, 125, -1, -1, -1,
        -1, 261, -1, -1, 264, -1, -1, -1, 268, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, 123,
        280, 125, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, 44,
        -1, -1, -1, -1, -1, 123, -1, 125, -1, -1,
        -1, -1, 256, 257, 258, -1, 260, 261, 262, 263,
        264, -1, -1, 267, 268, -1, 270, 271, 272, 273,
        -1, 275, 276, 277, 278, -1, 280, 256, 257, 258,
        -1, 260, 261, 262, 263, 264, -1, -1, 267, 268,
        -1, 270, 271, 272, 273, -1, 275, 276, 277, 278,
        -1, 280, 256, 257, 258, -1, 260, 261, 262, 263,
        264, -1, 44, 267, 268, -1, 270, 271, 123, -1,
        125, 275, 276, 277, 278, -1, 280, 256, 257, 258,
        -1, 260, 261, 262, 263, 264, 44, -1, 267, 268,
        -1, 270, 271, -1, -1, -1, 275, 276, 277, 278,
        -1, 280, 256, 257, 258, -1, 260, 261, 262, 263,
        264, -1, -1, 267, 268, -1, 270, 271, -1, -1,
        -1, 275, 276, 277, 278, -1, 280, -1, 256, 257,
        258, -1, 260, 261, 262, 263, 264, -1, -1, 267,
        268, 123, -1, 125, -1, -1, -1, 275, 276, 277,
        278, -1, 280, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, 123, -1, 125, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, 256, 257, 258, -1, 260, 261, 262, 263, 264,
        -1, -1, 267, 268, -1, -1, -1, -1, -1, -1,
        275, 276, 277, 278, -1, 280, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, 260, 261,
        -1, -1, 264, -1, -1, -1, 268, -1, -1, -1,
        -1, -1, -1, 275, 276, 277, 278, -1, 280, -1,
        -1, -1, 260, 261, -1, -1, 264, -1, -1, -1,
        268, -1, -1, -1, -1, -1, -1, 275, 276, 277,
        278, -1, 280,
    };
  }

  final static short YYFINAL = 11;
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
      "type_declaration : statement",
      "class_declaration : CLASS ID class_body",
      "class_declaration : CLASS ID interfaces class_body",
      "class_body : '{' class_body_declarations '}'",
      "class_body_declarations : class_body_declaration",
      "class_body_declarations : class_body_declarations class_body_declaration",
      "class_body_declaration : class_member_declaration",
      "class_member_declaration : field_declaration",
      "class_member_declaration : method_declaration",
      "field_declaration : type variable_declarators ','",
      "field_declaration : error ','",
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
      "method_declarator : ID '(' ')'",
      "method_body : block",
      "method_body : ','",
      "formal_parameter : type variable_declarator_id",
      "real_parameter : arithmetic_operation",
      "interfaces : IMPLEMENT interface_type_list",
      "interface_type_list : type_name",
      "interface_type_list : interface_type_list ';' type_name",
      "interface_declaration : INTERFACE ID interface_body",
      "interface_body : '{' interface_member_declaration '}'",
      "interface_body : '{' '}' ','",
      "interface_member_declaration : interface_method_declaration",
      "interface_member_declaration : interface_member_declaration interface_method_declaration",
      "interface_method_declaration : result_type method_declarator",
      "implement_for_declaration : IMPL FOR reference_type ':' implement_for_body",
      "implement_for_declaration : IMPL FOR reference_type ':' error ','",
      "implement_for_body : '{' implement_for_body_declarations '}'",
      "implement_for_body_declarations : implement_for_body_declaration",
      "implement_for_body_declarations : implement_for_body_declarations implement_for_body_declaration",
      "implement_for_body_declaration : implement_for_method_declaration",
      "implement_for_method_declaration : method_header implement_for_method_body",
      "implement_for_method_body : method_body",
      "assignment : left_hand_side assignment_operator expression",
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
      "if_then_statement : IF '(' expression ')' executable_statament error ','",
      "if_then_statement : IF '(' expression ')' error ','",
      "if_then_statement : IF '(' error ','",
      "if_then_else_statement : IF '(' expression ')' executable_block ELSE executable_block END_IF ','",
      "if_then_else_statement : IF '(' expression ')' executable_block ELSE executable_statament END_IF ','",
      "if_then_else_statement : IF '(' expression ')' executable_statament ELSE executable_statament END_IF ','",
      "if_then_else_statement : IF '(' expression ')' executable_statament ELSE executable_block END_IF ','",
      "if_then_else_statement : IF '(' expression ')' executable_statament ELSE executable_block ','",
      "for_in_range_statement : FOR for_variable IN RANGE '(' for_init for_end for_update ')' executable_block ','",
      "for_in_range_statement : FOR for_variable IN RANGE '(' for_init for_end for_update ')' executable_statament ','",
      "for_variable : ID",
      "for_init : factor",
      "for_update : factor",
      "for_end : factor",
      "print_statement : PRINT CADENA ','",
      "print_statement : PRINT",
  };

  // #line 351 "grammer.y"

  private static AnalizadorLexico aLexico;

  // This method is the one where BYACC/J expects to obtain its input tokens.
  // Wrap any file/string scanning code you have in this function. This method
  // should return <0 if there is an error, and 0 when it encounters the end of
  // input. See the examples to clarify what we mean.
  int yylex() {
    Tupla<String, Short> t = aLexico.generateToken();
    String lexema = t.getFirst();
    Short token = t.getSecond();

    System.out.println("Token: " + token + " Lexema: " + lexema);

    if (lexema != null)
      yylval = new ParserVal(lexema);

    AnalizadorLexico.lista_token.add(String.valueOf(token));
    return token;
  }

  // This method is expected by BYACC/J, and is used to provide error messages to
  // be directed to the channels the user desires.
  void yyerror(String msg) {
    System.out.println(msg);
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
      Logger.logWarning(aLexico.getTokenPosition(),
          "El DOUBLE se excedio de rango, el mismo fue truncado al valor " + RDN_MIN + ".");
      n_lexema = "" + RDN_MIN;
    }

    return n_lexema;
  }

  private String negarLong(String lexema) {

    String n_lexema = '-' + lexema;
    long numero = 0;

    try {
      numero = Long.parseLong(n_lexema);
    } catch (Exception ex) {
    }

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
        if (indice < directories.size() && indice >= 0) {
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

          if (indice < directories.size() && indice >= 0) {
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

  public static void main(String[] args) {
    System.out.println("Iniciando compilacion... ");

    String input = generatePath();

    aLexico = new AnalizadorLexico(input);

    if (!aLexico.hasReadWell()) {
      return;
    }

    Parser aSintactico = new Parser(false);
    aSintactico.run();
    aSintactico.dump_stacks(20);
    System.out.println("Lista de token reconocidos " + AnalizadorLexico.lista_token.toString());
    /*
     * String listaTOKENS = "";
     * for (int i = 0; i < 50; i++) {
     * listaTOKENS = listaTOKENS + " " + aLexico.generateToken();
     * int token = -1;
     * while (token != 0) {
     * token = aLexico.generateToken();
     * listaTOKENS = listaTOKENS + " " + token;
     * System.out.println(listaTOKENS);
     * }
     * System.out.println(TablaSimbolos.tablaSimbolos.toString());
     * }
     */

    Logger.logError(1, "Este es un error.");
    Logger.logWarning(2, "Esta es una advertencia.");

    Logger.dumpLog();
    System.out.println(aLexico.getProgram());
  }

  // #line 807 "Parser.java"
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
        case 43:
        // #line 131 "grammer.y"
        {
          Logger.logError(aLexico.getTokenPosition(), "Es necesario implementar el cuerpo del metodo.");
        }
          break;
        case 77:
        // #line 202 "grammer.y"
        {
          System.out.println(val_peek(0).sval);
          negarDouble(val_peek(0).sval);
        }
          break;
        case 78:
        // #line 203 "grammer.y"
        {
          System.out.println(val_peek(0).sval);
          negarLong(val_peek(0).sval);
        }
          break;
        case 79:
        // #line 204 "grammer.y"
        {
          Logger.logWarning(aLexico.getTokenPosition(), "Los tipos enteros deben ser sin signo.");
        }
          break;
        case 95:
        // #line 255 "grammer.y"
        {
          Logger.logError(aLexico.getTokenPosition(), "Es necesario declarar el retorno del bloque.");
        }
          break;
        case 97:
        // #line 257 "grammer.y"
        {
          Logger.logError(aLexico.getTokenPosition(), "Es necesario declarar el retorno del bloque.");
        }
          break;
        case 129:
        // #line 317 "grammer.y"
        {
          Logger.logError(aLexico.getTokenPosition(), "Es necesario declarar el final de la sentencia de control.");
        }
          break;
        case 130:
        // #line 318 "grammer.y"
        {
          Logger.logError(aLexico.getTokenPosition(), "Es necesario declarar el cuerpo de la sentencia de control.");
        }
          break;
        case 131:
        // #line 319 "grammer.y"
        {
          Logger.logError(aLexico.getTokenPosition(), "La condicion de la sentencia de control no es correcta.");
        }
          break;
        case 144:
        // #line 346 "grammer.y"
        {
          Logger.logError(aLexico.getTokenPosition(), "Se esperaba una cadena.");
        }
          break;
        // #line 996 "Parser.java"
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
