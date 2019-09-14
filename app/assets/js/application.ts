import Vue from 'vue'
import DeckBuild from './components/DeckBuild'

$(function() {
  new Vue({
    el: '#top',
    components: { "deck_build": DeckBuild }
  });
});
