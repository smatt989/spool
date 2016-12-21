import React from 'react';
import { render } from 'react-dom';
import PureRenderMixin from 'react-addons-pure-render-mixin';
import {connect} from 'react-redux';
import {Modal, Button, FormControl, Panel, Accordion, ButtonGroup, DropdownButton, MenuItem, InputGroup, Form, Col, Row} from 'react-bootstrap';
import {showProps} from '../utilities';
import {List, Map} from 'immutable';
import {ObjectVariableFormContainer} from './ObjectVariableForm';
import {IntegerVariableFormContainer} from './IntegerVariableForm';
import {addItemVariableAssignmentSlotInStagedTrigger} from '../action_creators';
import _ from 'lodash';

const VariableForm = React.createClass({
    mixins: [PureRenderMixin],
    getVariableDetails: function() {
        return this.props.variableDetails || null;
    },
    getCurrentAssignment: function() {
        return this.props.currentAssignment || List.of()
    },
    getItemKey: function() {
        return this.props.itemKey || null
    },
    getVariableIndex: function() {
        return this.props.index || null
    },
    getElementType: function() {
        return this.props.elementType || null
    },
    render: function() {

        const itemKey = this.getItemKey()
        const variableIndex = this.getVariableIndex()
        const addItemVariableAssignmentSlotInStagedTrigger = this.props.addItemVariableAssignmentSlotInStagedTrigger
        const elementType = this.getElementType()

        var variableSelection;
        var aNewVariable;

        if(this.getVariableDetails().get('variableArity') !== "one"){
            aNewVariable = <Button bsStyle="primary" onClick={() => addItemVariableAssignmentSlotInStagedTrigger(elementType, itemKey, variableIndex)}>Add another</Button>
        }

        var variableForms = [];
        if(this.getVariableDetails().get('variableType') === "object"){
            this.getCurrentAssignment().map(function(v, i){
                            variableForms.push(<ObjectVariableFormContainer elementType={elementType} itemKey={itemKey} selection={v} variableIndex={variableIndex} arrayIndex={i} key={i} />) })
        } else if(this.getVariableDetails().get('variableType') === "integer"){
            this.getCurrentAssignment().map(function(v, i){
                            variableForms.push(<IntegerVariableFormContainer elementType={elementType} itemKey={itemKey} selection={v} variableIndex={variableIndex} arrayIndex={i} key={i} />) })
        }

        return <div>
            <h4>{this.getVariableDetails().get('name')}</h4>
             {variableForms}
            {aNewVariable}
        </div>
    }
})

const mapDispatchToProps = (dispatch) => {
    return {
            addItemVariableAssignmentSlotInStagedTrigger: (itemType, itemKey, variableIndex) => {
                dispatch(addItemVariableAssignmentSlotInStagedTrigger(itemType, itemKey, variableIndex))
            }
    }
}

function mapStateToProps(state) {
  return {

  };
}

export const VariableFormContainer = connect(mapStateToProps, mapDispatchToProps)(VariableForm);