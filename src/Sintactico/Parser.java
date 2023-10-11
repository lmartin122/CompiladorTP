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
      0, 1, 1, 2, 2, 2, 2, 3, 3, 7,
      9, 9, 10, 11, 11, 12, 15, 15, 16, 16,
      17, 18, 13, 20, 22, 23, 23, 23, 23, 23,
      21, 21, 24, 26, 8, 27, 27, 4, 4, 29,
      29, 29, 29, 30, 30, 31, 31, 5, 5, 5,
      5, 33, 34, 34, 35, 36, 37, 38, 39, 39,
      41, 42, 42, 43, 44, 44, 44, 45, 45, 45,
      45, 45, 19, 46, 46, 46, 47, 47, 47, 47,
      47, 48, 48, 49, 49, 49, 50, 50, 50, 50,
      50, 50, 40, 40, 51, 51, 51, 51, 51, 51,
      14, 14, 52, 32, 53, 53, 54, 54, 55, 28,
      25, 25, 25, 25, 57, 57, 56, 56, 58, 58,
      6, 6, 59, 59, 59, 59, 59, 59, 60, 68,
      61, 61, 61, 61, 61, 61, 69, 69, 69, 66,
      70, 70, 67, 62, 62, 62, 62, 62, 62, 62,
      63, 63, 63, 63, 63, 63, 63, 63, 63, 64,
      64, 64, 64, 64, 71, 72, 74, 73, 65, 65,
      65,
  };
  final static short yylen[] = { 2,
      1, 1, 2, 1, 1, 1, 1, 3, 4, 3,
      1, 2, 1, 1, 1, 3, 1, 3, 1, 3,
      1, 1, 2, 2, 1, 4, 4, 6, 3, 3,
      1, 1, 2, 1, 2, 1, 3, 3, 4, 3,
      3, 2, 2, 1, 2, 2, 3, 5, 6, 6,
      4, 3, 1, 2, 1, 2, 1, 3, 1, 1,
      3, 1, 1, 1, 1, 3, 3, 1, 3, 3,
      3, 3, 1, 1, 3, 3, 1, 4, 3, 3,
      3, 1, 1, 1, 3, 2, 1, 1, 1, 2,
      2, 2, 1, 1, 4, 3, 6, 4, 3, 6,
      1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
      5, 3, 4, 2, 3, 2, 1, 2, 1, 2,
      1, 1, 1, 1, 1, 1, 1, 1, 2, 2,
      1, 1, 1, 1, 1, 1, 1, 1, 1, 2,
      1, 1, 1, 7, 7, 6, 6, 7, 7, 7,
      9, 9, 9, 9, 9, 9, 9, 9, 9, 12,
      12, 13, 8, 8, 1, 1, 1, 1, 3, 3,
      3,
  };
  final static short yydefred[] = { 0,
      0, 0, 0, 0, 0, 0, 0, 25, 108, 107,
      109, 0, 0, 143, 0, 0, 2, 4, 5, 6,
      7, 135, 0, 0, 0, 137, 0, 141, 0, 0,
      0, 142, 101, 103, 105, 106, 121, 122, 132, 133,
      134, 136, 139, 138, 0, 131, 0, 0, 104, 0,
      0, 0, 0, 165, 0, 0, 0, 0, 0, 0,
      0, 114, 117, 0, 3, 21, 0, 17, 0, 32,
      23, 31, 0, 24, 94, 93, 0, 0, 0, 129,
      140, 171, 0, 0, 0, 8, 0, 0, 0, 0,
      38, 0, 0, 83, 87, 88, 89, 0, 0, 0,
      0, 0, 0, 0, 77, 82, 84, 0, 0, 0,
      170, 169, 96, 34, 0, 0, 0, 0, 112, 118,
      0, 0, 0, 0, 58, 99, 0, 61, 0, 51,
      110, 0, 36, 0, 11, 13, 14, 15, 0, 102,
      9, 39, 42, 0, 0, 44, 43, 0, 0, 0,
      90, 92, 91, 86, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 95, 113, 0, 18, 20, 22, 30, 0, 0,
      29, 0, 0, 98, 0, 0, 53, 55, 0, 10,
      12, 0, 0, 46, 40, 45, 41, 0, 0, 0,
      0, 123, 124, 125, 126, 127, 128, 0, 85, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      79, 80, 81, 0, 0, 0, 48, 0, 111, 33,
      27, 0, 26, 0, 57, 56, 52, 54, 37, 16,
      47, 116, 0, 119, 0, 0, 0, 0, 78, 0,
      0, 0, 147, 0, 0, 146, 0, 166, 0, 50,
      49, 97, 0, 100, 115, 120, 0, 0, 149, 0,
      0, 150, 148, 0, 0, 144, 0, 0, 145, 0,
      0, 28, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 163, 164, 168, 0, 156, 157, 159, 158, 151,
      152, 155, 154, 153, 0, 167, 0, 0, 0, 160,
      161, 162,
  };
  final static short yydgoto[] = { 15,
      16, 17, 18, 19, 20, 21, 86, 87, 134, 135,
      136, 137, 22, 23, 67, 68, 69, 176, 114, 24,
      71, 25, 74, 180, 26, 115, 132, 133, 91, 145,
      146, 199, 130, 186, 187, 188, 236, 28, 29, 77,
      30, 31, 100, 101, 102, 103, 104, 105, 106, 107,
      32, 33, 34, 35, 36, 64, 200, 243, 201, 37,
      38, 202, 203, 204, 205, 206, 207, 45, 46, 47,
      55, 259, 295, 307,
  };
  final static short yysindex[] = { 26,
      -226, -224, -218, 35, -194, -167, -210, 0, 0, 0,
      0, 73, 49, 0, 0, 26, 0, 0, 0, 0,
      0, 0, -140, -9, -123, 0, 0, 0, 2, 119,
      130, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 143, 0, 147, 153, 0, 137,
      -109, -31, -40, 0, -50, -215, 178, 180, -34, -51,
      186, 0, 0, 55, 0, 0, 192, 0, 217, 0,
      0, 0, -12, 0, 0, 0, -15, -25, -21, 0,
      0, 0, 177, 32, 110, 0, 191, 274, -115, -39,
      0, 281, 288, 0, 0, 0, 0, -64, -19, 299,
      -151, 65, 86, 324, 0, 0, 0, 77, 289, 294,
      0, 0, 0, 0, 30, 86, 237, 320, 0, 0,
      -140, -15, 120, -3, 0, 0, 57, 0, 97, 0,
      0, 315, 0, 174, 0, 0, 0, 0, -140, 0,
      0, 0, 0, -206, -98, 0, 0, -22, 78, -11,
      0, 0, 0, 0, 335, 80, -15, -15, -15, -15,
      -15, -15, -15, -15, -11, -11, -11, 340, 177, -110,
      156, 0, 0, 278, 0, 0, 0, 0, -140, 295,
      0, 71, 157, 0, -9, -92, 0, 0, 32, 0,
      0, 64, 355, 0, 0, 0, 0, 89, 0, -94,
      92, 0, 0, 0, 0, 0, 0, 373, 0, -173,
      -36, 58, 65, 65, 86, 86, 86, 86, 324, 324,
      0, 0, 0, -44, 375, 381, 0, 385, 0, 0,
      0, 171, 0, 387, 0, 0, 0, 0, 0, 0,
      0, 0, 95, 0, 78, 386, 78, 391, 0, 393,
      78, 395, 0, 78, 397, 0, 388, 0, 383, 0,
      0, 0, 402, 0, 0, 0, 182, 183, 0, 185,
      190, 0, 0, 194, 195, 0, -208, 196, 0, 78,
      -2, 0, 417, 418, 419, 420, 421, 422, 423, 425,
      426, 0, 0, 0, 413, 0, 0, 0, 0, 0,
      0, 0, 0, 0, -2, 0, 433, 109, -27, 0,
      0, 0,
  };
  final static short yyrindex[] = { 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 11, 0, 0, 0, 475, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 12, 0, 0, -43,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 432, 0, 103, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      436, -37, 25, 111, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 50, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 356, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, -14, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 18, 37, 134, 138, 151, 161, 122, 145,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
      0, 0,
  };
  final static short yygindex[] = { 0,
      0, 462, 0, 0, 0, 31, 394, 0, 0, 346,
      0, 0, -8, -6, 343, 362, 305, 0, -46, -90,
      300, -7, 342, 363, 21, 410, 0, 301, 0, 399,
      13, 455, 198, 0, 306, 0, 0, 0, 0, 0,
      0, 0, 392, 0, 221, -17, 238, 43, 0, -201,
      0, 0, 0, 0, 0, 0, -144, 0, 193, 0,
      0, 24, 36, 51, 68, 121, 135, 0, 0, 0,
      0, 0, 0, 0,
  };
  final static int YYTABLESIZE = 589;
  static short yytable[];
  static {
    yytable();
  }

  static void yytable() {
    yytable = new short[] { 99,
        98, 147, 63, 65, 98, 99, 113, 253, 90, 143,
        98, 211, 129, 85, 99, 126, 312, 60, 197, 98,
        99, 154, 258, 39, 99, 98, 195, 124, 99, 98,
        125, 62, 237, 98, 70, 40, 39, 181, 185, 39,
        109, 116, 98, 63, 72, 57, 59, 289, 40, 193,
        41, 40, 48, 49, 290, 51, 104, 62, 66, 116,
        116, 52, 76, 41, 49, 68, 41, 42, 58, 14,
        172, 104, 59, 73, 53, 177, 138, 67, 139, 294,
        42, 144, 144, 42, 68, 49, 68, 39, 171, 250,
        73, 89, 14, 73, 120, 185, 56, 184, 14, 40,
        267, 256, 270, 306, 116, 48, 274, 240, 73, 277,
        123, 233, 59, 13, 41, 183, 179, 179, 157, 158,
        43, 14, 121, 14, 161, 138, 162, 139, 163, 232,
        164, 42, 14, 43, 44, 292, 43, 144, 14, 66,
        144, 215, 216, 217, 218, 226, 19, 44, 13, 84,
        44, 74, 14, 74, 74, 74, 73, 196, 78, 8,
        196, 19, 75, 310, 75, 75, 75, 245, 246, 74,
        74, 13, 74, 62, 71, 79, 8, 13, 72, 119,
        75, 75, 8, 75, 43, 76, 80, 76, 76, 76,
        81, 69, 208, 71, 83, 71, 82, 72, 44, 72,
        198, 70, 198, 76, 76, 72, 76, 221, 222, 223,
        69, 257, 69, 242, 108, 92, 151, 152, 153, 265,
        70, 111, 70, 112, 88, 251, 252, 48, 93, 117,
        60, 198, 65, 65, 93, 8, 95, 96, 97, 94,
        95, 96, 97, 93, 178, 94, 95, 96, 97, 93,
        121, 48, 8, 93, 94, 95, 96, 97, 128, 59,
        94, 95, 96, 97, 94, 95, 96, 97, 94, 95,
        96, 97, 9, 10, 11, 75, 49, 122, 95, 96,
        97, 1, 2, 3, 104, 59, 4, 66, 66, 5,
        104, 102, 6, 7, 68, 68, 68, 68, 190, 129,
        8, 9, 10, 11, 60, 12, 67, 67, 61, 4,
        60, 131, 5, 85, 118, 4, 7, 142, 5, 254,
        255, 149, 7, 8, 9, 10, 11, 150, 12, 8,
        9, 10, 11, 60, 12, 210, 159, 160, 4, 156,
        4, 5, 168, 5, 60, 7, 169, 7, 212, 4,
        60, 170, 5, 247, 248, 4, 7, 12, 5, 12,
        167, 173, 7, 174, 309, 165, 225, 227, 12, 4,
        166, 8, 5, 189, 12, 209, 7, 213, 214, 224,
        74, 74, 74, 74, 8, 9, 10, 11, 12, 49,
        244, 75, 75, 75, 75, 9, 10, 11, 241, 49,
        219, 220, 229, 71, 71, 71, 71, 72, 72, 72,
        72, 228, 234, 249, 76, 76, 76, 76, 260, 231,
        69, 69, 69, 69, 261, 262, 263, 264, 280, 269,
        70, 70, 70, 70, 272, 266, 273, 268, 276, 271,
        279, 281, 282, 275, 283, 284, 278, 285, 8, 9,
        10, 11, 286, 49, 27, 50, 287, 288, 291, 54,
        296, 297, 298, 299, 300, 301, 302, 27, 303, 304,
        27, 305, 293, 308, 1, 130, 64, 65, 35, 191,
        141, 192, 175, 230, 235, 194, 182, 127, 148, 239,
        155, 238, 0, 0, 0, 0, 0, 0, 0, 0,
        311, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        110, 0, 0, 0, 0, 0, 0, 0, 27, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 140,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 140, 140, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 140,
    };
  }

  static short yycheck[];
  static {
    yycheck();
  }

  static void yycheck() {
    yycheck = new short[] { 40,
        45, 41, 46, 41, 45, 40, 41, 44, 40, 125,
        45, 156, 123, 123, 40, 41, 44, 61, 41, 45,
        40, 41, 224, 0, 40, 45, 125, 40, 40, 45,
        77, 46, 125, 45, 44, 0, 13, 41, 129, 16,
        256, 59, 45, 13, 24, 256, 61, 256, 13, 256,
        0, 16, 279, 280, 263, 280, 46, 46, 41, 77,
        78, 280, 61, 13, 280, 41, 16, 0, 279, 44,
        41, 61, 61, 280, 40, 122, 85, 41, 85, 281,
        13, 89, 90, 16, 60, 280, 62, 64, 59, 263,
        41, 123, 44, 44, 64, 186, 264, 41, 44, 64,
        245, 44, 247, 305, 122, 279, 251, 44, 59, 254,
        123, 41, 40, 123, 64, 59, 123, 124, 270, 271,
        0, 44, 59, 44, 60, 134, 62, 134, 43, 59,
        45, 64, 44, 13, 0, 280, 16, 145, 44, 280,
        148, 159, 160, 161, 162, 256, 44, 13, 123, 259,
        16, 41, 44, 43, 44, 45, 280, 145, 40, 275,
        148, 59, 41, 308, 43, 44, 45, 262, 263, 59,
        60, 123, 62, 125, 41, 46, 275, 123, 41, 125,
        59, 60, 275, 62, 64, 41, 44, 43, 44, 45,
        44, 41, 150, 60, 58, 62, 44, 60, 64, 62,
        123, 41, 123, 59, 60, 185, 62, 165, 166, 167,
        60, 256, 62, 125, 265, 256, 281, 282, 283, 125,
        60, 44, 62, 44, 256, 262, 263, 279, 269, 44,
        274, 123, 270, 271, 269, 275, 281, 282, 283, 280,
        281, 282, 283, 269, 125, 280, 281, 282, 283, 269,
        59, 279, 275, 269, 280, 281, 282, 283, 280, 274,
        280, 281, 282, 283, 280, 281, 282, 283, 280, 281,
        282, 283, 276, 277, 278, 274, 280, 61, 281, 282,
        283, 256, 257, 258, 274, 274, 261, 270, 271, 264,
        280, 280, 267, 268, 270, 271, 272, 273, 125, 123,
        275, 276, 277, 278, 256, 280, 270, 271, 260, 261,
        256, 280, 264, 123, 260, 261, 268, 44, 264, 262,
        263, 41, 268, 275, 276, 277, 278, 40, 280, 275,
        276, 277, 278, 256, 280, 256, 272, 273, 261, 41,
        261, 264, 266, 264, 256, 268, 58, 268, 156, 261,
        256, 58, 264, 262, 263, 261, 268, 280, 264, 280,
        37, 125, 268, 44, 256, 42, 169, 170, 280, 261,
        47, 275, 264, 59, 280, 41, 268, 157, 158, 40,
        270, 271, 272, 273, 275, 276, 277, 278, 280, 280,
        198, 270, 271, 272, 273, 276, 277, 278, 44, 280,
        163, 164, 125, 270, 271, 272, 273, 270, 271, 272,
        273, 256, 256, 41, 270, 271, 272, 273, 44, 125,
        270, 271, 272, 273, 44, 41, 256, 41, 41, 44,
        270, 271, 272, 273, 44, 243, 44, 245, 44, 247,
        44, 59, 41, 251, 263, 263, 254, 263, 275, 276,
        277, 278, 263, 280, 0, 1, 263, 263, 263, 5,
        44, 44, 44, 44, 44, 44, 44, 13, 44, 44,
        16, 59, 280, 41, 0, 44, 41, 16, 123, 134,
        87, 139, 121, 179, 185, 144, 124, 78, 90, 189,
        99, 186, -1, -1, -1, -1, -1, -1, -1, -1,
        308, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        56, -1, -1, -1, -1, -1, -1, -1, 64, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, 85,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, 123, 124, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, 134,
    };
  }

  final static short YYFINAL = 15;
  final static short YYMAXTOKEN = 283;
  final static String yyname[] = {
      "end-of-file", null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, "'%'", null, null, "'('", "')'", "'*'", "'+'",
      "','", "'-'", "'.'", "'/'", null, null, null, null, null, null, null, null, null, null, "':'",
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
      "method_declarator : ID '(' formal_parameter ';' error ')'",
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
      "interface_body : '{' interface_member_declaration '}'",
      "interface_body : '(' interface_member_declaration ')'",
      "interface_body : '{' '}'",
      "interface_body : '(' ')'",
      "interface_member_declaration : interface_method_declaration",
      "interface_member_declaration : interface_member_declaration interface_method_declaration",
      "interface_method_declaration : result_type method_declarator",
      "interface_method_declaration : result_type error ','",
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
      "left_hand_side : reference_type",
      "left_hand_side : field_acces",
      "field_acces : primary '.' ID",
      "primary : reference_type",
      "primary : field_acces",
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
      "term : '(' ')'",
      "factor : CTE_DOUBLE",
      "factor : CTE_UINT",
      "factor : CTE_LONG",
      "factor : '-' CTE_DOUBLE",
      "factor : '-' CTE_LONG",
      "factor : '-' CTE_UINT",
      "assignment_operator : '='",
      "assignment_operator : MINUS_ASSIGN",
      "method_invocation : ID '(' real_parameter ')'",
      "method_invocation : ID '(' ')'",
      "method_invocation : ID '(' real_parameter ';' error ')'",
      "method_invocation : field_acces '(' real_parameter ')'",
      "method_invocation : field_acces '(' ')'",
      "method_invocation : field_acces '(' real_parameter ';' error ')'",
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
      "expression_statement : statement_expression ','",
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
      "for_in_range_statement : FOR for_variable IN RANGE '(' for_init ';' for_end ';' for_update ')' executable_block",
      "for_in_range_statement : FOR for_variable IN RANGE '(' for_init ';' for_end ';' for_update ')' executable_statament",
      "for_in_range_statement : FOR for_variable IN RANGE '(' for_init ';' for_end ';' for_update ')' error ','",
      "for_in_range_statement : FOR for_variable IN RANGE '(' error ')' executable_block",
      "for_in_range_statement : FOR for_variable IN RANGE '(' error ')' executable_statament",
      "for_variable : reference_type",
      "for_init : factor",
      "for_update : factor",
      "for_end : factor",
      "print_statement : PRINT CADENA ','",
      "print_statement : PRINT error ','",
      "print_statement : error CADENA ','",
  };

  // #line 383 "grammer.y"

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

    double RDN_MIN = -2.2250738585072014D * Math.pow(10, 308);
    double RDN_MAX = -1.7976931348623157D * Math.pow(10, 308);

    System.out.println("Numero dentro de negar doble: " + lexema);

    double number = 0.0;
    String n_lexema;

    try {
      number = -Double.parseDouble(lexema);
    } catch (Exception ex) {
    }

    System.out.println("Numero dentro de negar doble: " + number);

    if (number > RDN_MAX || number < RDN_MIN) {
      Logger.logWarning(aLexico.getProgramPosition(),
          "El DOUBLE se excedio de rango, el mismo fue truncado al valor " + RDN_MAX + ".");
      n_lexema = String.valueOf(RDN_MAX);
    }

    n_lexema = String.valueOf(number);

    addTablaSimbolos(lexema, n_lexema, "D");

    return n_lexema;
  }

  private void addTablaSimbolos(String lexema, String n_lexema, String tipo) {

    if (!TablaSimbolos.containsKey(n_lexema)) {

      if (tipo == "D") { // Perdon Luis por hacer un if por tipos
        TablaSimbolos.addDouble(n_lexema);
      } else {
        TablaSimbolos.addLong(n_lexema);
      }
      TablaSimbolos.addContador(n_lexema);
    } else {
      TablaSimbolos.increaseCounter(n_lexema);
    }

    TablaSimbolos.decreaseCounter(lexema);
  }

  private String ChequeoRangoEntero(String lexema) {

    long RDN_MAX = (long) Math.pow(2, 31);
    long number = 0;

    try {
      number = Long.parseLong(lexema);
    } catch (Exception ex) {
    }

    if (number >= RDN_MAX) {
      Logger.logWarning(aLexico.getProgramPosition(),
          "El LONG se excedio de rango, el mismo fue truncado al valor " + RDN_MAX + ".");
      TablaSimbolos.decreaseCounter(lexema);
      lexema = String.valueOf(RDN_MAX - 1);
      TablaSimbolos.addContador(lexema);
    }

    return lexema;
  }

  private String negarLong(String lexema) {

    long number = 0;

    try {
      number = -Long.parseLong(lexema);
    } catch (Exception ex) {
    }

    String n_lexema = String.valueOf(number);

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

        try {
          indice = Integer.parseInt(input);
        } catch (Exception ex) {
          indice = -1;
        }

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

          try {
            indice = Integer.parseInt(input);
          } catch (Exception ex) {
            indice = -1;
          }

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

  // #line 835 "Parser.java"
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
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio el programa");
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
        case 16:
        // #line 67 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una declaracion de atributo/s.");
        }
          break;
        case 26:
        // #line 93 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un metodo.");
        }
          break;
        case 27:
        // #line 94 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(),
              "La declaracion de un metodo debe estar limitado por parentesis \"(...)\".");
        }
          break;
        case 28:
        // #line 95 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Solo se permite la declaracion de un unico parametro formal.");
        }
          break;
        case 29:
        // #line 96 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un metodo.");
        }
          break;
        case 30:
        // #line 97 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(),
              "La declaracion de un metodo debe estar limitado por parentesis \"(...)\".");
        }
          break;
        case 38:
        // #line 119 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una INTERFACE.");
        }
          break;
        case 39:
        // #line 120 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Es necesario definir el cuerpo de la interface.");
        }
          break;
        case 41:
        // #line 124 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(),
              "El cuerpo de la interface debe estar limitado por llaves \"{...}\".");
        }
          break;
        case 43:
        // #line 126 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(),
              "El cuerpo de la interface debe estar limitado por llaves \"{...}\".");
        }
          break;
        case 47:
        // #line 134 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Es necesario generar la declaracion del metodo");
        }
          break;
        case 48:
        // #line 137 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio un IMPL FOR.");
        }
          break;
        case 49:
        // #line 138 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Es necesario implementar el cuerpo del metodo.");
        }
          break;
        case 50:
        // #line 139 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Se debe referenciar a una clase.");
        }
          break;
        case 51:
        // #line 140 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Declaracion de IMPL FOR no valida.");
        }
          break;
        case 58:
        // #line 164 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una asignacion.");
        }
          break;
        case 64:
        // #line 178 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion logica.");
        }
          break;
        case 73:
        // #line 193 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una operacion aritmetica.");
        }
          break;
        case 86:
        // #line 214 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Termino vacio.");
        }
          break;
        case 89:
        // #line 219 "grammer.y"
        {
          yyval = new ParserVal(ChequeoRangoEntero(val_peek(0).sval));
        }
          break;
        case 90:
        // #line 220 "grammer.y"
        {
          System.out.println("Posicion 1: " + val_peek(1).sval + ", Posicion 2: " + val_peek(0).sval);
          yyval = new ParserVal(negarDouble(val_peek(0).sval));
        }
          break;
        case 91:
        // #line 221 "grammer.y"
        {
          System.out.println(val_peek(0).sval);
          yyval = new ParserVal(negarLong(val_peek(0).sval));
        }
          break;
        case 92:
        // #line 222 "grammer.y"
        {
          Logger.logWarning(aLexico.getProgramPosition(), "Los tipos enteros deben ser sin signo.");
          yyval = new ParserVal(val_peek(0).sval);
        }
          break;
        case 95:
        // #line 229 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo, con pj de parametro.");
        }
          break;
        case 96:
        // #line 230 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una invocacion a un metodo, sin pj de parametro.");
        }
          break;
        case 97:
        // #line 231 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Solo se permite el pasaje de un parametro real.");
        }
          break;
        case 98:
        // #line 232 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(),
              "Se reconocio una invocacion a un metodo desde una clase, con pj de parametro.");
        }
          break;
        case 99:
        // #line 233 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(),
              "Se reconocio una invocacion a un metodo desde una clase, sin pj de parametro.");
        }
          break;
        case 100:
        // #line 234 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Solo se permite el pasaje de un parametro real.");
        }
          break;
        case 112:
        // #line 276 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");
        }
          break;
        case 114:
        // #line 278 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Es necesario declarar el retorno del bloque.");
        }
          break;
        case 129:
        // #line 306 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una declaracion de variable local.");
        }
          break;
        case 144:
        // #line 337 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF.");
        }
          break;
        case 145:
        // #line 338 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF.");
        }
          break;
        case 146:
        // #line 339 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(),
              "Es necesario declarar el final de la sentencia de control IF.");
        }
          break;
        case 147:
        // #line 340 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(),
              "Es necesario declarar el final de la sentencia de control IF.");
        }
          break;
        case 148:
        // #line 341 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(),
              "Es necesario declarar el cuerpo de la sentencia de control IF.");
        }
          break;
        case 149:
        // #line 342 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");
        }
          break;
        case 150:
        // #line 343 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");
        }
          break;
        case 151:
        // #line 347 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");
        }
          break;
        case 152:
        // #line 348 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");
        }
          break;
        case 153:
        // #line 349 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");
        }
          break;
        case 154:
        // #line 350 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia IF ELSE.");
        }
          break;
        case 155:
        // #line 351 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(),
              "Es necesario declarar el END_IF de la sentencia de control IF.");
        }
          break;
        case 156:
        // #line 352 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");
        }
          break;
        case 157:
        // #line 353 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");
        }
          break;
        case 158:
        // #line 354 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");
        }
          break;
        case 159:
        // #line 355 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "La condicion de la sentencia de control IF no es correcta.");
        }
          break;
        case 160:
        // #line 359 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia FOR IN RANGE.");
        }
          break;
        case 161:
        // #line 360 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia FOR IN RANGE.");
        }
          break;
        case 162:
        // #line 361 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Cuerpo del FOR IN RANGE no valido.");
        }
          break;
        case 163:
        // #line 362 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Condicion del FOR IN RANGE no valido.");
        }
          break;
        case 164:
        // #line 363 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Condicion del FOR IN RANGE no valido.");
        }
          break;
        case 169:
        // #line 378 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Se reconocio una sentencia PRINT.");
        }
          break;
        case 170:
        // #line 379 "grammer.y"
        {
          Logger.logError(aLexico.getProgramPosition(), "Se esperaba una cadena.");
        }
          break;
        case 171:
        // #line 380 "grammer.y"
        {
          Logger.logRule(aLexico.getProgramPosition(), "Declaracion de PRINT no valida.");
        }
          break;
        // #line 1220 "Parser.java"
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
