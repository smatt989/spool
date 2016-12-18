import React from 'react';
import { render } from 'react-dom';
import PureRenderMixin from 'react-addons-pure-render-mixin';
import {connect} from 'react-redux';
import {Modal, Button, FormControl, Panel, Accordion, ButtonGroup, DropdownButton, MenuItem, InputGroup, Form, Col, Row} from 'react-bootstrap';
import {showProps} from '../utilities';
import _ from 'lodash';

export const TriggerElement = React.createClass({
  mixins: [PureRenderMixin],
  getOptions: function() {
    return this.props.options || [];
  },
  getElementType: function() {
    return this.props.elementType
  },
  componentWillMount: function() {
    this.setState({selected: null})
  },
  updateSelection: function(selection){
    this.setState({selected: selection})
  },
  optionByTitle: function(title){
    const options = this.getOptions()
    return _.find(options, function(o){ return o.title === title})
  },
  render: function(){

    const updateSelection = this.updateSelection
    const optionByTitle = this.optionByTitle

    var triggerSelection = <div></div>;

    if(this.state.selected && this.state.selected.variables){
        triggerSelection = <div>
                              {this.state.selected.variables.map(function(variable, i){
                                return <VariableSelectionContainer key={i} variableDetails={variable} />})}
                           </div>
    }

    return <div>
              <FormControl componentClass="select" placeholder="select" onChange={(a)=> updateSelection(optionByTitle(a.target.value))}>
                <option value="select">select {this.getElementType()}</option>
                {this.getOptions().map((o) =>
                    <option key={o.title} value={o.title}>{o.title}</option>)}
              </FormControl>
              {triggerSelection}
           </div>
  }

});

const VariableSelection = React.createClass({
    mixins: [PureRenderMixin],
    getVariableDetails: function() {
        return this.props.variableDetails || null
    },
    componentWillMount: function(){
        this.state = {selections: [null]};
    },
    makeSelection: function(selection, index){
        var selections = _.cloneDeep(this.state.selections)
        selections[index] = selection
        this.setState({selections: selections});
    },
    addAnotherSelectionSlot: function(){
        var selections = _.cloneDeep(this.state.selections)
        selections[selections.length] = null
        this.setState({selections: selections})
    },
    deleteSelectionSlot: function(index){
        const selections = this.state.selections
        this.setState({selections: _.filter(selections, function(o, i){return i !== index})});
    },
    getNextIndex: function() {
        return this.state ? this.state.selections.length : 0;
    },
    render: function() {

        const addAnotherSelectionSlot = this.addAnotherSelectionSlot
        const makeSelection = this.makeSelection

        const markers = this.props.markers

        const deleteSelectionSlot = this.deleteSelectionSlot

        var variableSelection;
        var aNewVariable;

        if(this.getVariableDetails().variableType === "object"){
            if(this.getVariableDetails().variableArity !== "one"){
                aNewVariable = <Button onClick={addAnotherSelectionSlot}>Add another</Button>
            }
        }

        return <div>
            <h4>{this.getVariableDetails().name}</h4>
            {this.state.selections.map(function(s, i){
                return <ObjectSelection deleteSelectionSlot={deleteSelectionSlot} makeSelection={makeSelection} index={i} selection={s} key={i} objects={markers} />
            })}
            {aNewVariable}
        </div>
    }
})

const ObjectSelection = React.createClass({
    mixins: [PureRenderMixin],
    getObjects: function() {
        return this.props.objects || [];
    },
    getSelection: function() {
        return this.props.selection || null;
    },
    getIndex: function() {
        return this.props.index || 0;
    },
    render: function() {

        const selected = this.getSelection() ? this.getSelection() : "select"
        const makeSelection = this.props.makeSelection
        const index = this.getIndex()
        const deleteSelectionSlot = this.props.deleteSelectionSlot

        const deleteButton = index > 0 ? <Button onClick={() => deleteSelectionSlot(index)}>Delete</Button> : ""

        return <div>
                <Row>
                <Form horizontal>
                  <Col sm={10}>
                  <FormControl value={selected} componentClass="select" placeholder="select" onChange={(a) => makeSelection(a.target.value, index)}>
                    <option key={-1} value="select">select object</option>
                    {this.getObjects().map((o) =>
                        <option key={o.title} value={o.key}>{o.title}</option>)}
                  </FormControl>
                  </Col>
                  <Col sm={2}>
                  {deleteButton}
                  </Col>
                </Form>
                </Row>
               </div>
    }
})

function mapStateToProps(state) {
  return {
    markers: state.get('markers')
  };
}

export const VariableSelectionContainer = connect(mapStateToProps)(VariableSelection);