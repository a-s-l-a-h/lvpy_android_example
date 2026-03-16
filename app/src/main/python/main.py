"""
examples/counter.py — lvpy counter with flexbox

"""
import lvpy as lv

lv.lvpy_init()
lv.lvpy_display_init(390, 844, resizable=True, title="Counter")
lv.lvpy_ui_init(300)   # ← auto, responsive
#lv.lvpy_set_scale(1.0)

dp  = lv.lvpy_dp
fs  = lv.lvpy_font_size
sfn = lv.lvpy_set_font
pct = lv.lvpy_pct

BG    = 0x0D1117
CARD  = 0x161B22
GREEN = 0x3FB950
RED   = 0xFF6B6B
WHITE = 0xF0F6FC
MUTED = 0x8B949E
DIM   = 0x444466

AL = lv.ALIGN
EV = lv.EVENT
SB = lv.SCROLLBAR
FL = lv.FLAG

FF_COL = 1
FF_ROW = 0
FA_C   = 2
FA_SE  = 3

# ── screen ───────────────────────────────────────────────
scr = lv.lvpy_scr_create()
scr.set_style_bg_color(BG, 0)
scr.set_scrollbar_mode(SB.OFF)
scr.remove_flag(FL.SCROLLABLE)
scr.set_style_pad_all(dp(16), 0)
lv.obj_set_flex_flow(scr, FF_COL)
lv.obj_set_flex_align(scr, FA_C, FA_C, FA_C)
lv.lvpy_scr_load(scr)

# ── card — pct for both width and height ─────────────────
card = lv.Obj.create(scr)
card.set_size(pct(90), pct(70))   # always 90% x 70% of screen
card.set_style_bg_color(CARD, 0)
card.set_style_radius(dp(24), 0)
card.set_style_border_width(0, 0)
card.set_style_shadow_width(dp(20), 0)
card.set_style_shadow_color(0x000000, 0)
card.set_style_pad_all(dp(20), 0)
card.set_scrollbar_mode(SB.OFF)
card.remove_flag(FL.SCROLLABLE)
lv.obj_set_flex_flow(card, FF_COL)
lv.obj_set_flex_align(card, FA_C, FA_C, FA_C)
card.set_style_pad_row(dp(12), 0)

# ── title ─────────────────────────────────────────────────
title = lv.label_create(card)
lv.label_set_text(title, "Counter")
sfn(title, fs(20))
title.set_style_text_color(MUTED, 0)

# ── counter display ───────────────────────────────────────
count = [0]
count_lbl = lv.label_create(card)
lv.label_set_text(count_lbl, "0")
sfn(count_lbl, fs(64))
count_lbl.set_style_text_color(WHITE, 0)

# ── status ────────────────────────────────────────────────
status_lbl = lv.label_create(card)
lv.label_set_text(status_lbl, "tap to count")
sfn(status_lbl, fs(12))
status_lbl.set_style_text_color(MUTED, 0)

# ── divider ───────────────────────────────────────────────
div = lv.Obj.create(card)
div.set_size(pct(80), dp(1))
div.set_style_bg_color(DIM, 0)
div.set_style_border_width(0, 0)
div.set_style_radius(0, 0)
div.remove_flag(FL.SCROLLABLE)

# ── button row — pct height adapts to card ───────────────
btn_row = lv.Obj.create(card)
btn_row.set_size(pct(100), pct(14))  # 14% of card height
btn_row.set_style_bg_opa(0, 0)
btn_row.set_style_border_width(0, 0)
btn_row.set_style_pad_all(0, 0)
btn_row.set_style_pad_column(dp(10), 0)
btn_row.set_scrollbar_mode(SB.OFF)
btn_row.remove_flag(FL.SCROLLABLE)
lv.obj_set_flex_flow(btn_row, FF_ROW)
lv.obj_set_flex_align(btn_row, FA_SE, FA_C, FA_C)

def make_btn(text, color):
    b = lv.btn_create(btn_row)
    b.set_size(pct(28), pct(100))   # 28% wide, full btn_row height
    b.set_style_bg_color(color, 0)
    b.set_style_radius(dp(16), 0)
    b.set_style_border_width(0, 0)
    b.set_style_shadow_width(dp(8), 0)
    b.set_style_shadow_color(color, 0)
    bl = lv.label_create(b)
    lv.label_set_text(bl, text)
    sfn(bl, fs(24))
    bl.set_style_text_color(WHITE, 0)
    bl.center()
    return b

btn_minus = make_btn("-", RED)
btn_reset = make_btn("R", DIM)
btn_plus  = make_btn("+", GREEN)

# ── scale info ────────────────────────────────────────────
info = lv.label_create(scr)
lv.label_set_text(info, f"scale={lv.lvpy_get_scale():.2f}")
sfn(info, fs(10))
info.set_style_text_color(DIM, 0)

# ── callbacks ────────────────────────────────────────────
def update():
    lv.label_set_text(count_lbl, str(count[0]))
    if count[0] > 0:
        count_lbl.set_style_text_color(GREEN, 0)
        lv.label_set_text(status_lbl, f"positive  +{count[0]}")
        status_lbl.set_style_text_color(GREEN, 0)
    elif count[0] < 0:
        count_lbl.set_style_text_color(RED, 0)
        lv.label_set_text(status_lbl, f"negative  {count[0]}")
        status_lbl.set_style_text_color(RED, 0)
    else:
        count_lbl.set_style_text_color(WHITE, 0)
        lv.label_set_text(status_lbl, "tap to count")
        status_lbl.set_style_text_color(MUTED, 0)

def on_plus():  count[0] += 1; update()
def on_minus(): count[0] -= 1; update()
def on_reset(): count[0] = 0;  update()

btn_plus.add_event_cb(on_plus,   EV.CLICKED)
btn_minus.add_event_cb(on_minus, EV.CLICKED)
btn_reset.add_event_cb(on_reset, EV.CLICKED)

lv.lvpy_run()