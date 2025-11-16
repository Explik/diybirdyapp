"""
Config Viewer Component
Reusable component for displaying language configurations in a table.
"""

import streamlit as st
import pandas as pd
from shared.language_client import LanguageClient, CONFIG_TYPES


def render_config_viewer(
    client: LanguageClient,
    selected_language_id: str,
    selected_config_type: str = "all",
    show_type_filter: bool = True
):
    """
    Render a configuration viewer table for a selected language.
    
    Args:
        client: LanguageClient instance for API calls
        selected_language_id: ID of the language to show configs for
        selected_config_type: Type filter ("all" or specific config type)
        show_type_filter: Whether to show the configuration type filter dropdown
    
    Returns:
        tuple: (selected_config_type, configs) where selected_config_type may be updated
               if show_type_filter is True
    """
    
    if show_type_filter:
        # Configuration type filter
        config_type_options = {"all": "All configs"} | {k: v for k, v in CONFIG_TYPES.items()}
        selected_config_type = st.selectbox(
            "Configuration Type",
            options=list(config_type_options.keys()),
            format_func=lambda x: config_type_options[x],
            key="config_type_filter",
            index=list(config_type_options.keys()).index(selected_config_type)
        )
    
    # Display configurations
    with st.spinner("Loading configurations..."):
        try:
            # Get configs with optional type filter
            filter_type = None if selected_config_type == "all" else selected_config_type
            configs = client.get_language_configs(selected_language_id, filter_type)
            
            if configs:
                # Prepare data for display
                df_configs = pd.DataFrame([{
                    "ID": config.get('id', 'N/A'),
                    "Type": CONFIG_TYPES.get(config.get('type', ''), config.get('type', 'Unknown')),
                    "Details": str({k: v for k, v in config.items() if k not in ['id', 'type']}),
                    "EditLink": f"/Update_Config?languageId={selected_language_id}&configType={config.get('type', '')}&configId={config.get('id', '')}"
                } for config in configs])
                
                st.dataframe(
                    df_configs,
                    use_container_width=True,
                    hide_index=True,
                    column_config={
                        "EditLink": st.column_config.LinkColumn("", display_text="Edit")
                    }
                )
            else:
                st.info("No configurations found for this language.")
            
            return selected_config_type, configs
                
        except Exception as e:
            st.error(f"Failed to fetch configurations: {str(e)}")
            return selected_config_type, []
